/*
 * @(#)awt_MenuItem.cpp	1.67 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#include "awt_MenuItem.h"
#include "awt_Menu.h"
#include "awt_MenuBar.h"
#include "awt_DesktopProperties.h"
#include <sun_awt_windows_WCheckboxMenuItemPeer.h>

// Begin -- Win32 SDK include files
#include <tchar.h>
#include <imm.h>
#include <ime.h>
// End -- Win32 SDK include files

//add for multifont menuitem
#include <java_awt_CheckboxMenuItem.h>
#include <java_awt_Toolkit.h>
#include <java_awt_event_InputEvent.h>

/* IMPORTANT! Read the README.JNI file for notes on JNI converted AWT code.
 */

/************************************************************************
 * AwtMenuItem fields
 */

HBITMAP AwtMenuItem::bmpCheck;
jobject AwtMenuItem::systemFont;

jfieldID AwtMenuItem::labelID;
jfieldID AwtMenuItem::enabledID;
jfieldID AwtMenuItem::fontID;
jfieldID AwtMenuItem::privateKeyID;
jfieldID AwtMenuItem::appContextID;
jfieldID AwtMenuItem::shortcutLabelID;
jfieldID AwtMenuItem::isCheckboxID;
jfieldID AwtMenuItem::stateID;

jmethodID AwtMenuItem::getDefaultFontMID;

// Added by waleed to initialize the RTL Flags
LANGID AwtMenuItem::m_idLang = LOWORD(GetKeyboardLayout(0));
UINT AwtMenuItem::m_CodePage =
    AwtMenuItem::LangToCodePage(AwtMenuItem::m_idLang);
BOOL AwtMenuItem::m_isWin95 = IS_WIN95;
BOOL AwtMenuItem::sm_rtl = PRIMARYLANGID(GetInputLanguage()) == LANG_ARABIC ||
                           PRIMARYLANGID(GetInputLanguage()) == LANG_HEBREW;
BOOL AwtMenuItem::sm_rtlReadingOrder = 
    PRIMARYLANGID(GetInputLanguage()) == LANG_ARABIC;

/************************************************************************
 * AwtMenuItem methods
 */

AwtMenuItem::AwtMenuItem() {
    m_peerObject = NULL;
    m_menuContainer = NULL;
    m_Id = (UINT)-1;
    m_freeId = FALSE;
}

AwtMenuItem::~AwtMenuItem() {
    if (m_freeId && _tcscmp(GetClassName(), TEXT("SunAwtMenuItem")) == 0) {
	AwtToolkit::GetInstance().RemoveCmdID( GetID() );
    }

    CriticalSection::Lock l(GetLock());
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);
    if (m_peerObject != NULL) {
	JNI_SET_PDATA(m_peerObject, NULL);
	env->DeleteGlobalRef(m_peerObject);
    }
}

LPCTSTR AwtMenuItem::GetClassName() {
  return TEXT("SunAwtMenuItem");
}
// Convert Language ID to CodePage
UINT AwtMenuItem::LangToCodePage(LANGID idLang)
{
    TCHAR strCodePage[MAX_ACP_STR_LEN];
    // use the LANGID to create a LCID
    LCID idLocale = MAKELCID(idLang, SORT_DEFAULT);
    // get the ANSI code page associated with this locale
    if (GetLocaleInfo(idLocale, LOCALE_IDEFAULTANSICODEPAGE, strCodePage, sizeof(strCodePage)/sizeof(TCHAR)) > 0 )
        return _ttoi(strCodePage);
    else
        return GetACP();
}

/*
 * Link the C++, Java peer together
 */
void AwtMenuItem::LinkObjects(JNIEnv *env, jobject peer)
{
    m_peerObject = env->NewGlobalRef(peer);
    JNI_SET_PDATA(peer, this);
}

AwtMenuItem* AwtMenuItem::Create(jobject peer, jobject menuPeer)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    jobject target = NULL;
    AwtMenuItem* item = NULL;

    try {
        if (env->EnsureLocalCapacity(1) < 0) {
	    return NULL;
	}
	PDATA pData;
	JNI_CHECK_PEER_RETURN_NULL(menuPeer);

	/* target is a java.awt.MenuItem  */
	target = env->GetObjectField(peer, AwtObject::targetID);

	AwtMenu* menu = (AwtMenu *)pData;
	item = new AwtMenuItem();

	item->LinkObjects(env, peer);
	item->SetMenuContainer(menu);
	item->SetNewID();
	menu->AddItem(item);
    } catch (...) {
        env->DeleteLocalRef(target);
        throw;
    }

    env->DeleteLocalRef(target);
    return item;
}

MsgRouting AwtMenuItem::WmNotify(UINT notifyCode)
{
    return mrDoDefault;
}

// This function returns a local reference
jobject
AwtMenuItem::GetFont(JNIEnv *env)
{
    jobject self = GetPeer(env);
    jobject target = env->GetObjectField(self, AwtObject::targetID);
    jobject font = JNU_CallMethodByName(env, 0, target, "getFont_NoClientCode", "()Ljava/awt/Font;").l;

    if (font == NULL) {
        font = env->NewLocalRef(GetDefaultFont(env));
    }

    env->DeleteLocalRef(target);
    return font;
}

jobject
AwtMenuItem::GetDefaultFont(JNIEnv *env) {
    if (AwtMenuItem::systemFont == NULL) {
        jclass cls = env->FindClass("sun/awt/windows/WMenuItemPeer");
        DASSERT(cls != NULL);

        AwtMenuItem::systemFont =
            env->CallStaticObjectMethod(cls, AwtMenuItem::getDefaultFontMID);
        DASSERT(AwtMenuItem::systemFont);

        AwtMenuItem::systemFont = env->NewGlobalRef(AwtMenuItem::systemFont);
    }
    return AwtMenuItem::systemFont;
}

void
AwtMenuItem::DrawSelf(DRAWITEMSTRUCT& drawInfo)
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);
    if (env->EnsureLocalCapacity(4) < 0) {
	return;
    }

    // self is sun.awt.windows.WMenuItemPeer
    jobject self = GetPeer(env);

    //  target is java.awt.MenuItem
    jobject target = env->GetObjectField(self, AwtObject::targetID);

    HDC hDC = drawInfo.hDC;
    RECT rect = drawInfo.rcItem;
    RECT textRect = rect;
    SIZE size;

    DWORD crBack,crText;
    HBRUSH hbrBack;

    jobject font = GetFont(env);
    jstring text = GetJavaString(env);
    size = AwtFont::getMFStringSize(hDC, font, text);

    /* 4700350: If the font size is taller than the menubar, change to the
     * default font.  Otherwise, menu text is painted over the title bar and
     * client area.  -bchristi
     */
    if (IsTopMenu() && size.cy > ::GetSystemMetrics(SM_CYMENU)) {
        env->DeleteLocalRef(font);
        font = env->NewLocalRef(GetDefaultFont(env));
        size = AwtFont::getMFStringSize(hDC, font, text);
    }

    /* Fix for bug 4257944 by ssi@sparc.spb.su
    * check state of the parent
    */
    AwtMenu* menu = GetMenuContainer();
    DASSERT(menu != NULL && GetID() >= 0);

    //Check whether the MenuItem is disabled.
    BOOL bEnabled = (jboolean)env->GetBooleanField(target,
						   AwtMenuItem::enabledID);
    if (menu != NULL) {
        bEnabled = bEnabled && !menu->IsDisabledAndPopup();
    }

    if ((drawInfo.itemState) & (ODS_SELECTED)) {
	// Set background and text colors for selected item
	crBack = ::GetSysColor (COLOR_HIGHLIGHT);
        // Disabled text must be drawn in gray.
	crText = ::GetSysColor(bEnabled? COLOR_HIGHLIGHTTEXT : COLOR_GRAYTEXT);
    } else {
        // COLOR_MENUBAR is only defined on WindowsXP. Our binaries are
        // built on NT, hence the below ifdef.

#ifndef COLOR_MENUBAR
#define COLOR_MENUBAR 30
#endif
	// Set background and text colors for unselected item
        if (IS_WINXP && IsTopMenu() && AwtDesktopProperties::IsXPStyle()) {
            crBack = ::GetSysColor (COLOR_MENUBAR);
        } else {
            crBack = ::GetSysColor (COLOR_MENU);
        }
        // Disabled text must be drawn in gray.
	crText = ::GetSysColor (bEnabled ? COLOR_MENUTEXT : COLOR_GRAYTEXT);
    }

    // Fill item rectangle with background color
    hbrBack = ::CreateSolidBrush (crBack);
    DASSERT(hbrBack);
    VERIFY(::FillRect (hDC, &rect, hbrBack));
    VERIFY(::DeleteObject (hbrBack));

    // Set current background and text colors
    ::SetBkColor (hDC, crBack);
    ::SetTextColor (hDC, crText);

    int nOldBkMode = ::SetBkMode(hDC, OPAQUE);
    DASSERT(nOldBkMode != 0);

    //draw check mark
    int checkWidth = ::GetSystemMetrics(SM_CXMENUCHECK);
    jboolean isCheckbox =
	(jboolean)env->GetBooleanField(self, AwtMenuItem::isCheckboxID);

    if (isCheckbox) {
    // means that target is a java.awt.CheckboxMenuItem
	jboolean state = (jboolean)env->GetBooleanField(target,
							AwtMenuItem::stateID);
	if (state) {
            DASSERT(drawInfo.itemState & ODS_CHECKED);
            RECT checkRect;
            ::CopyRect(&checkRect, &textRect);
            if (GetRTL())
	        checkRect.left = checkRect.right - checkWidth;
	    else
	        checkRect.right = checkRect.left + checkWidth;

            DrawCheck(hDC, checkRect);
	}
    }

    ::SetBkMode(hDC, TRANSPARENT);
    int x = 0;
    //draw string
    if (!IsTopMenu()){
        textRect.left += checkWidth;
	x = (GetRTL()) ? textRect.right - checkWidth - size.cx : textRect.left;
    } else {
        x = textRect.left = (textRect.left + textRect.right - size.cx) / 2;
    }
    
    int y = (textRect.top+textRect.bottom-size.cy)/2;

    // Text must be drawn in emboss if the Menu is disabled and not selected.
    BOOL bEmboss = !bEnabled && !(drawInfo.itemState & ODS_SELECTED);
    if (bEmboss) {
        ::SetTextColor(hDC, GetSysColor(COLOR_BTNHILIGHT));
        AwtFont::drawMFString(hDC, font, text, x + 1, y + 1, GetCodePage());
        ::SetTextColor(hDC, GetSysColor(COLOR_BTNSHADOW));
    }
    AwtFont::drawMFString(hDC, font, text, x, y, GetCodePage());

    jstring shortcutLabel =
	(jstring)env->GetObjectField(self, AwtMenuItem::shortcutLabelID);
    if (!IsTopMenu() && shortcutLabel != NULL) {
        UINT oldAlign = 0;
	if (GetRTL()){
	    oldAlign = ::SetTextAlign(hDC, TA_LEFT);
	    AwtFont::drawMFString(hDC, font, shortcutLabel, textRect.left, y,
				  GetCodePage());
	} else {
	    oldAlign = ::SetTextAlign(hDC, TA_RIGHT);
	    AwtFont::drawMFString(hDC, font, shortcutLabel,
				  textRect.right - checkWidth, y, 
				  GetCodePage());
	}

        ::SetTextAlign(hDC, oldAlign);
    }

    VERIFY(::SetBkMode(hDC,nOldBkMode));

    env->DeleteLocalRef(target);
    env->DeleteLocalRef(text);
    env->DeleteLocalRef(font);
    env->DeleteLocalRef(shortcutLabel);
}


void AwtMenuItem::DrawItem(DRAWITEMSTRUCT& drawInfo)
{
    DASSERT(drawInfo.CtlType == ODT_MENU);

    if (drawInfo.itemID != m_Id)
        return;

    /* Fixed bug 4349969. Since the problem occurs on Windows 98 and not on 
       Windows NT, the fix is to check for Windows 95/98 and to check if the
       handle to the menu of the item to be drawn is the same as the handle to the
       menu of the menu object. If they're not the same, just return and don't do
       the drawing.
    */
    if ( IS_WIN95 && drawInfo.hwndItem != (HWND)this->m_menuContainer->GetHMenu()) {
        return;
    } else
        DrawSelf(drawInfo);
}

void AwtMenuItem::MeasureSelf(HDC hDC, MEASUREITEMSTRUCT& measureInfo)
{
    JNIEnv *env =(JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);
    if (env->EnsureLocalCapacity(4) < 0) {
	return;
    }

    /* self is a sun.awt.windows.WMenuItemPeer */
    jobject self = GetPeer(env);

    /* font is a java.awt.Font */
    jobject font = GetFont(env);
    jstring text = GetJavaString(env);
    SIZE size = AwtFont::getMFStringSize(hDC, font, text);

    /* 4700350: If the font size is taller than the menubar, change to the
     * default font.  Otherwise, menu text is painted over the title bar and
     * client area.  -bchristi
     */
    if (IsTopMenu() && size.cy > ::GetSystemMetrics(SM_CYMENU)) {
        jobject defFont = GetDefaultFont(env);
        env->DeleteLocalRef(font);
        font = env->NewLocalRef(defFont);
        size = AwtFont::getMFStringSize(hDC, font, text);
    }

    jstring fontName =
	(jstring)JNU_CallMethodByName(env, 0,font, "getName",
				      "()Ljava/lang/String;").l;
    /* fontMetrics is a Hsun_awt_windows_WFontMetrics */
    jobject fontMetrics =  GetFontMetrics(env, font);


//     int height = env->GetIntField(fontMetrics, AwtFont::heightID);
    int height = (jint)JNU_CallMethodByName(env, 0, fontMetrics, "getHeight",
                                            "()I").i;

    measureInfo.itemHeight = height;
    measureInfo.itemHeight += measureInfo.itemHeight/3;
    // 3 is a heuristic number
    measureInfo.itemWidth = size.cx;
    if (!IsTopMenu()) {
        int checkWidth = ::GetSystemMetrics(SM_CXMENUCHECK);
	measureInfo.itemWidth += checkWidth;

        // Add in shortcut width, if one exists.
	jstring shortcutLabel =
	    (jstring)env->GetObjectField(self, AwtMenuItem::shortcutLabelID);
        if (shortcutLabel != NULL) {
            size = AwtFont::getMFStringSize(hDC, font, shortcutLabel);
            measureInfo.itemWidth += size.cx + checkWidth;
	    env->DeleteLocalRef(shortcutLabel);
        }
    }
    env->DeleteLocalRef(text);
    env->DeleteLocalRef(font);
    env->DeleteLocalRef(fontName);
    env->DeleteLocalRef(fontMetrics);
}

void AwtMenuItem::MeasureItem(HDC hDC, MEASUREITEMSTRUCT& measureInfo)
{
    DASSERT(measureInfo.CtlType == ODT_MENU);

    if (measureInfo.itemID != m_Id)
	return;

    MeasureSelf(hDC, measureInfo);
}

jobject AwtMenuItem::GetFontMetrics(JNIEnv *env, jobject font)
{
    static jobject toolkit = NULL;
    if (toolkit == NULL) {
        if (env->PushLocalFrame(2) < 0)
	    return NULL;
	jclass cls = env->FindClass("java/awt/Toolkit");
	jobject toolkitLocal =
	    env->CallStaticObjectMethod(cls, AwtToolkit::getDefaultToolkitMID);
	toolkit = env->NewGlobalRef(toolkitLocal);
	DASSERT(!safe_ExceptionOccurred(env));
	env->PopLocalFrame(0);
    }
    /*
    JNU_PrintClass(env, "toolkit", toolkit);
    JNU_PrintClass(env, "font", font);

    jclass cls = env->FindClass("java/awt/Toolkit");
    jmethodID mid = env->GetMethodID(cls, "getFontMetrics",
				     "(Ljava/awt/Font;)Ljava/awt/FontMetrics;");
    jstring fontName =
	(jstring)JNU_CallMethodByName(env, 0,font, "getName",
				      "()Ljava/lang/String;").l;
    JNU_PrintString(env, "font name", fontName);

    fprintf(stderr, "mid: %x\n", mid);
    fprintf(stderr, "cached mid: %x\n", AwtToolkit::getFontMetricsMID);
    DASSERT(!safe_ExceptionOccurred(env));
    */
    jobject fontMetrics =
      env->CallObjectMethod(toolkit, AwtToolkit::getFontMetricsMID, font);
    DASSERT(!safe_ExceptionOccurred(env));

    return fontMetrics;
}

BOOL AwtMenuItem::IsTopMenu()
{
    return FALSE;
}

void AwtMenuItem::DrawCheck(HDC hDC, RECT rect)
{
    if (bmpCheck == NULL) {
        bmpCheck = ::LoadBitmap(AwtToolkit::GetInstance().GetModuleHandle(),
                                TEXT("CHECK_BITMAP"));
        DASSERT(bmpCheck != NULL);
    }

#define BM_SIZE 26  /* height and width of check.bmp */

    // Square the rectangle, so the check is proportional.
    int width = rect.right - rect.left;
    int diff = max(rect.bottom - rect.top - width, 0) ;
    int bottom = diff / 2;
    rect.bottom -= bottom;
    rect.top += diff - bottom;

    HDC hdcBitmap = ::CreateCompatibleDC(hDC);
    DASSERT(hdcBitmap != NULL);
    HBITMAP hbmSave = (HBITMAP)::SelectObject(hdcBitmap, bmpCheck);
    VERIFY(::StretchBlt(hDC, rect.left, rect.top,
                        rect.right - rect.left, rect.bottom - rect.top,
                        hdcBitmap, 0, 0, BM_SIZE, BM_SIZE, SRCCOPY));
    ::SelectObject(hdcBitmap, hbmSave);
    VERIFY(::DeleteDC(hdcBitmap));
}

void AwtMenuItem::DoCommand()
{
    JNIEnv *env = (JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2);

    // peer is sun.awt.windows.WMenuItemPeer
    jobject peer = GetPeer(env);

    jboolean isCheckbox =
      (jboolean)env->GetBooleanField(peer, AwtMenuItem::isCheckboxID);
    if (isCheckbox) {
        UINT nState = ::GetMenuState(GetMenuContainer()->GetHMenu(),
                                     GetID(), MF_BYCOMMAND);
        DASSERT(nState != 0xFFFFFFFF);
        DoCallback("handleAction", "(Z)V", ((nState & MF_CHECKED) == 0));
    } else {
        DoCallback("handleAction", "(JI)V", nowMillisUTC(),
                   (jint)AwtComponent::GetJavaModifiers());
    }
}

void AwtMenuItem::SetLabel(LPCTSTR sb)
{
    AwtMenu* menu = GetMenuContainer();
    /* Fix for bug 4257944 by ssi@sparc.spb.su
    * check parent
    */
    if (menu == NULL) return;
    DASSERT(menu != NULL && GetID() >= 0);

/*
 * SetMenuItemInfo is replaced by this code for fix bug 4261935
 */
    HMENU hMenu = menu->GetHMenu();
    MENUITEMINFO mii, mii1;

    // get full information about menu item
    memset(&mii, 0, sizeof(MENUITEMINFO));
    mii.cbSize = sizeof(MENUITEMINFO);
    mii.fMask = MIIM_CHECKMARKS | MIIM_DATA | MIIM_ID
              | MIIM_STATE | MIIM_SUBMENU | MIIM_TYPE;

    ::GetMenuItemInfo(hMenu, GetID(), FALSE, &mii);

    mii.fType = MFT_OWNERDRAW;
    mii.dwTypeData = (LPTSTR)(*sb);

    // find index by menu item id
    int nMenuItemCount = ::GetMenuItemCount(hMenu);;
    int idx;
    for (idx = 0; (idx < nMenuItemCount); idx++) {
	memset(&mii1, 0, sizeof(MENUITEMINFO));
        mii1.cbSize = sizeof mii1;
        mii1.fMask = MIIM_ID;
        ::GetMenuItemInfo(hMenu, idx, TRUE, &mii1);
        if (mii.wID == mii1.wID) break;
    }

    ::RemoveMenu(hMenu, idx, MF_BYPOSITION);
    ::InsertMenuItem(hMenu, idx, TRUE, &mii);

    // Redraw menu bar if it was affected.
    if (menu->GetMenuBar() == menu) {
        ::DrawMenuBar(menu->GetOwnerHWnd());
    }
}

void AwtMenuItem::Enable(BOOL isEnabled)
{
    AwtMenu* menu = GetMenuContainer();
    /* Fix for bug 4257944 by ssi@sparc.spb.su
    * check state of the parent
    */
    if (menu == NULL) return;
    isEnabled = isEnabled && !menu->IsDisabledAndPopup();
    DASSERT(menu != NULL && GetID() >= 0);
    VERIFY(::EnableMenuItem(menu->GetHMenu(), GetID(),
                            MF_BYCOMMAND | (isEnabled ? MF_ENABLED : MF_GRAYED))
           != 0xFFFFFFFF);

    // Redraw menu bar if it was affected.
    if (menu->GetMenuBar() == menu) {
        ::DrawMenuBar(menu->GetOwnerHWnd());
    }
}

void AwtMenuItem::SetState(BOOL isChecked)
{
    AwtMenu* menu = GetMenuContainer();
    /* Fix for bug 4257944 by ssi@sparc.spb.su
    * check parent
    */
    if (menu == NULL) return;
    DASSERT(menu != NULL && GetID() >= 0);
    VERIFY(::CheckMenuItem(menu->GetHMenu(), GetID(),
                           MF_BYCOMMAND | (isChecked ? MF_CHECKED : MF_UNCHECKED))
           != 0xFFFFFFFF);

    // Redraw menu bar if it was affected.
    if (menu->GetMenuBar() == menu) {
        ::DrawMenuBar(menu->GetOwnerHWnd());
    }
}

LRESULT AwtMenuItem::WinThreadExecProc(ExecuteArgs * args)
{
    switch( args->cmdId ) {
	case MENUITEM_SETLABEL:
	{
	    LPCTSTR sb = (LPCTSTR)args->param1;
	    DASSERT(!IsBadStringPtr(sb, 20));
	    this->SetLabel(sb);
	}
	break;

	case MENUITEM_ENABLE:
	{
	    BOOL	isEnabled = (BOOL)args->param1;
	    this->Enable(isEnabled);
	}
	break;

	case MENUITEM_SETSTATE:
	{
	    BOOL	isChecked = (BOOL)args->param1;
	    this->SetState(isChecked);
	}
	break;

	case MENUITEM_DISPOSE:
	{
            scheduleDelete();
	}
	break;

	default:
	    AwtObject::WinThreadExecProc(args);
	    break;
    }
    return 0L;
}


/************************************************************************
 * MenuComponent native methods
 */

extern "C" {

JNIEXPORT void JNICALL
Java_java_awt_MenuComponent_initIDs(JNIEnv *env, jclass cls)
{
    TRY;

    AwtMenuItem::fontID = env->GetFieldID(cls, "font", "Ljava/awt/Font;");
    AwtMenuItem::privateKeyID = env->GetFieldID(cls, "privateKey", "Ljava/lang/Object;");
    AwtMenuItem::appContextID = env->GetFieldID(cls, "appContext", "Lsun/awt/AppContext;");

    DASSERT(AwtMenuItem::fontID != NULL);
    DASSERT(AwtMenuItem::privateKeyID != NULL);

    CATCH_BAD_ALLOC;
}

} /* extern "C" */


/************************************************************************
 * MenuItem native methods
 */

extern "C" {

JNIEXPORT void JNICALL
Java_java_awt_MenuItem_initIDs(JNIEnv *env, jclass cls)
{
    TRY;

    AwtMenuItem::labelID = env->GetFieldID(cls, "label", "Ljava/lang/String;");
    AwtMenuItem::enabledID = env->GetFieldID(cls, "enabled", "Z");

    DASSERT(AwtMenuItem::labelID != NULL);
    DASSERT(AwtMenuItem::enabledID != NULL);

    CATCH_BAD_ALLOC;
}

} /* extern "C" */


/************************************************************************
 * CheckboxMenuItem fields
 */

extern "C" {

JNIEXPORT void JNICALL
Java_java_awt_CheckboxMenuItem_initIDs(JNIEnv *env, jclass cls)
{
    TRY;

    AwtMenuItem::stateID = env->GetFieldID(cls, "state", "Z");

    DASSERT(AwtMenuItem::stateID != NULL);

    CATCH_BAD_ALLOC;
}

} /* extern "C" */


/************************************************************************
 * WMenuItemPeer native methods
 */

extern "C" {

/*
 * Class:     sun_awt_windows_WMenuItemPeer
 * Method:    _setLabel
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WMenuItemPeer_initIDs(JNIEnv *env, jclass cls)
{
    TRY;

    AwtMenuItem::isCheckboxID = env->GetFieldID(cls, "isCheckbox", "Z");
    AwtMenuItem::shortcutLabelID = env->GetFieldID(cls, "shortcutLabel",
						   "Ljava/lang/String;");
    AwtMenuItem::getDefaultFontMID =
	env->GetStaticMethodID(cls, "getDefaultFont", "()Ljava/awt/Font;");

    DASSERT(AwtMenuItem::isCheckboxID != NULL);
    DASSERT(AwtMenuItem::shortcutLabelID != NULL);
    DASSERT(AwtMenuItem::getDefaultFontMID != NULL);

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WMenuItemPeer
 * Method:    _setLabel
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WMenuItemPeer__1setLabel(JNIEnv *env, jobject self,
					      jstring label)
{
    TRY;

    PDATA pData;
    JNI_CHECK_PEER_RETURN(self);

    // fix for bug 4251036 MenuItem setLabel(null/"") behaves differently 
    // under Win32 and Solaris
    if (JNU_IsNull (env, label)) {
        label = JNU_NewStringPlatform(env, TEXT(""));
    }
    LPCTSTR labelPtr = JNU_GetStringPlatformChars(env, label, 0);

    if (labelPtr == NULL) {
        throw std::bad_alloc();
    }

    AwtObject::WinThreadExec(self, AwtMenuItem::MENUITEM_SETLABEL,
			     (LPARAM)labelPtr);
    JNU_ReleaseStringPlatformChars(env, label, labelPtr);

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WMenuItemPeer
 * Method:    create
 * Signature: (Lsun/awt/windows/WMenuPeer;)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WMenuItemPeer_create(JNIEnv *env, jobject self,
					  jobject menu)
{
    TRY;

    JNI_CHECK_NULL_RETURN(menu, "null Menu");
    AwtToolkit::CreateComponent(self, menu,
				(AwtToolkit::ComponentFactory)
				AwtMenuItem::Create);
    PDATA pData;
    JNI_CHECK_PEER_CREATION_RETURN(self);

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WMenuItemPeer
 * Method:    enable
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WMenuItemPeer_enable(JNIEnv *env, jobject self,
					  jboolean on)
{
    TRY;

    PDATA pData;
    JNI_CHECK_PEER_RETURN(self);
    AwtObject::WinThreadExec(self, AwtMenuItem::MENUITEM_ENABLE, (LPARAM)on );

    CATCH_BAD_ALLOC;
}

/*
 * Class:     sun_awt_windows_WMenuItemPeer
 * Method:    _dispose
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WMenuItemPeer__1dispose(JNIEnv *env, jobject self)
{
    TRY_NO_HANG;

    PDATA pData;
    JNI_CHECK_PEER_RETURN(self);
    AwtMenuItem *p = (AwtMenuItem *)pData;

    if (p != NULL) {
	AwtObject::WinThreadExec(self, AwtMenuItem::MENUITEM_DISPOSE);
    }

    CATCH_BAD_ALLOC;
}

} /* extern "C" */

/************************************************************************
 * WCheckboxMenuItemPeer native methods
 */

extern "C" {

/*
 * Class:     sun_awt_windows_WCheckboxMenuItemPeer
 * Method:    setState
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_windows_WCheckboxMenuItemPeer_setState(JNIEnv *env, jobject self,
						    jboolean on)
{
    TRY;

    PDATA pData;
    JNI_CHECK_PEER_RETURN(self);
    AwtObject::WinThreadExec(self, AwtMenuItem::MENUITEM_SETSTATE, (LPARAM)on);

    CATCH_BAD_ALLOC;
}

} /* extern "C" */
