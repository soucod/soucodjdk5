#ifdef USE_PRAGMA_IDENT_SRC
#pragma ident "@(#)phase.cpp	1.50 03/12/23 16:42:53 JVM"
#endif
/*
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

#include "incls/_precompiled.incl"
#include "incls/_phase.cpp.incl"

const char * const Phase::names[Phase::last_phase] = {
  "Compiler",
  "Parse",
  "Optimistic",
  "Global Value Numbering",
  "Instruction Selection",
  "Copy Elimination",
  "Dead Code Elimination",
  "Conditional Constant Propagation",
  "CFG",
  "DefUse",
  "Register Allocation",
  "LIVE",
  "Interference Graph",
  "Coalesce",
  "Conditional Constant Propagation",
  "Find Idealized Loops"
};

elapsedTimer Phase::_t_totalCompilation;
elapsedTimer Phase::_t_methodCompilation;
elapsedTimer Phase::_t_nativeCompilation;
elapsedTimer Phase::_t_stubCompilation;
elapsedTimer Phase::_t_adapterCompilation;

elapsedTimer Phase::_t_parser;
elapsedTimer Phase::_t_optimizer;
elapsedTimer   Phase::_t_iterGVN;
elapsedTimer   Phase::_t_idealLoop;
elapsedTimer   Phase::_t_ccp;
elapsedTimer   Phase::_t_iterGVN2;
elapsedTimer Phase::_t_graphReshaping;
elapsedTimer Phase::_t_matcher;
elapsedTimer Phase::_t_scheduler;
elapsedTimer Phase::_t_registerAllocation;
elapsedTimer   Phase::_t_ctorChaitin;
elapsedTimer   Phase::_t_buildIFGphysical;
elapsedTimer   Phase::_t_computeLive;
elapsedTimer   Phase::_t_regAllocSplit;
elapsedTimer   Phase::_t_postAllocCopyRemoval;
elapsedTimer   Phase::_t_fixupSpills;
elapsedTimer Phase::_t_removeEmptyBlocks;
elapsedTimer Phase::_t_peephole;
elapsedTimer Phase::_t_codeGeneration;
elapsedTimer Phase::_t_registerMethod;
elapsedTimer Phase::_t_temporaryTimer1;
elapsedTimer Phase::_t_temporaryTimer2;
elapsedTimer Phase::_t_output;
elapsedTimer   Phase::_t_instrSched;
elapsedTimer   Phase::_t_buildOopMaps;

int Phase::_total_bytes_compiled = 0;
int Phase::_total_nmethods_size = 0;
int Phase::_total_code_size = 0;

//------------------------------Phase------------------------------------------
Phase::Phase( PhaseNumber pnum ) : _pnum(pnum), C( pnum == Compiler ? NULL : Compile::current()) { 
  // Poll for requests from shutdown mechanism to quiesce comiler (4448539, 4448544).
  // This is an effective place to poll, since the compiler is full of phases.
  // In particular, every inlining site uses a recursively created Parse phase.
  CompileBroker::maybe_block();
}

//------------------------------Phase------------------------------------------

void Phase::add_code_size(nmethod* nm) {
  _total_nmethods_size += nm->size(); 
  _total_code_size += nm->code_size();
}

#ifndef PRODUCT
static const double minimum_reported_time             = 0.0001; // seconds
static const double expected_method_compile_coverage  = 0.97;   // %
static const double minimum_meaningful_method_compile = 2.00;   // seconds

void Phase::print_timers() {
  tty->print_cr ("Accumulated compiler times:");
  tty->print_cr ("---------------------------");
  tty->print_cr ("  Total compilation: %3.3f sec.", Phase::_t_totalCompilation.seconds());
  tty->print    ("    method compilation : %3.3f sec", Phase::_t_methodCompilation.seconds());
  tty->print    ("/%d bytes",_total_bytes_compiled);
  tty->print_cr (" (%3.0f bytes per sec) ", Phase::_total_bytes_compiled / Phase::_t_methodCompilation.seconds());
  tty->print_cr ("       [%3.1f nmethod per method code bytes - including debug info etc..] ", (float)Phase::_total_nmethods_size / (float)Phase::_total_bytes_compiled );
  tty->print_cr ("       [%3.1f nmethod assembly bytes per method bytes] ", (float)Phase::_total_code_size / (float)Phase::_total_bytes_compiled );
  tty->print_cr ("    native compilation : %3.3f sec.", Phase::_t_nativeCompilation.seconds());
  tty->print_cr ("    stub compilation   : %3.3f sec.", Phase::_t_stubCompilation.seconds());
  tty->print_cr ("    adapter compilation: %3.3f sec.", Phase::_t_adapterCompilation.seconds());
  tty->print_cr ("  Phases:");
  tty->print_cr ("    parse        : %3.3f sec", Phase::_t_parser.seconds());
  tty->print_cr ("    optimizer    : %3.3f sec", Phase::_t_optimizer.seconds());
  if( Verbose || WizardMode ) {
    tty->print_cr ("      iterGVN      : %3.3f sec", Phase::_t_iterGVN.seconds());
    tty->print_cr ("      idealLoop    : %3.3f sec", Phase::_t_idealLoop.seconds());
    tty->print_cr ("      ccp          : %3.3f sec", Phase::_t_ccp.seconds());
    tty->print_cr ("      iterGVN2     : %3.3f sec", Phase::_t_iterGVN2.seconds());
    tty->print_cr ("      graphReshape : %3.3f sec", Phase::_t_graphReshaping.seconds());
    double optimizer_subtotal = Phase::_t_iterGVN.seconds() + 
      Phase::_t_idealLoop.seconds() + Phase::_t_ccp.seconds() + 
      Phase::_t_graphReshaping.seconds();
    double percent_of_optimizer = ((optimizer_subtotal == 0.0) ? 0.0 : (optimizer_subtotal / Phase::_t_optimizer.seconds() * 100.0));
    tty->print_cr ("      subtotal     : %3.3f sec,  %3.2f %%", optimizer_subtotal, percent_of_optimizer);
  }
  tty->print_cr ("    matcher      : %3.3f sec", Phase::_t_matcher.seconds());
  tty->print_cr ("    scheduler    : %3.3f sec", Phase::_t_scheduler.seconds());
  tty->print_cr ("    regalloc     : %3.3f sec", Phase::_t_registerAllocation.seconds());
  if( Verbose || WizardMode ) {
    tty->print_cr ("      ctorChaitin  : %3.3f sec", Phase::_t_ctorChaitin.seconds());
    tty->print_cr ("      buildIFG     : %3.3f sec", Phase::_t_buildIFGphysical.seconds());
    tty->print_cr ("      computeLive  : %3.3f sec", Phase::_t_computeLive.seconds());
    tty->print_cr ("      regAllocSplit: %3.3f sec", Phase::_t_regAllocSplit.seconds());
    tty->print_cr ("      postAllocCopyRemoval: %3.3f sec", Phase::_t_postAllocCopyRemoval.seconds());
    tty->print_cr ("      fixupSpills  : %3.3f sec", Phase::_t_fixupSpills.seconds());
    double regalloc_subtotal = Phase::_t_ctorChaitin.seconds() + 
      Phase::_t_buildIFGphysical.seconds() + Phase::_t_computeLive.seconds() + 
      Phase::_t_regAllocSplit.seconds()    + Phase::_t_fixupSpills.seconds() +
      Phase::_t_postAllocCopyRemoval.seconds();
    double percent_of_regalloc = ((regalloc_subtotal == 0.0) ? 0.0 : (regalloc_subtotal / Phase::_t_registerAllocation.seconds() * 100.0));
    tty->print_cr ("      subtotal     : %3.3f sec,  %3.2f %%", regalloc_subtotal, percent_of_regalloc);
  }
  tty->print_cr ("    removeEmpty  : %3.3f sec", Phase::_t_removeEmptyBlocks.seconds());
  tty->print_cr ("    peephole     : %3.3f sec", Phase::_t_peephole.seconds());
  tty->print_cr ("    codeGen      : %3.3f sec", Phase::_t_codeGeneration.seconds());
  tty->print_cr ("    install_code : %3.3f sec", Phase::_t_registerMethod.seconds());
  tty->print_cr ("    ------------ : ----------");
  double phase_subtotal = Phase::_t_parser.seconds() + 
    Phase::_t_optimizer.seconds() + Phase::_t_graphReshaping.seconds() + 
    Phase::_t_matcher.seconds() + Phase::_t_scheduler.seconds() + 
    Phase::_t_registerAllocation.seconds() + 
    Phase::_t_removeEmptyBlocks.seconds() + Phase::_t_peephole.seconds() + 
    Phase::_t_codeGeneration.seconds() + Phase::_t_registerMethod.seconds();
  double percent_of_method_compile = ((phase_subtotal == 0.0) ? 0.0 : phase_subtotal / Phase::_t_methodCompilation.seconds()) * 100.0;
  // counters inside Compile::CodeGen include time for adapters and stubs
  // so phase-total can be greater than 100%
  tty->print_cr ("    total        : %3.3f sec,  %3.2f %%", phase_subtotal, percent_of_method_compile);

  assert( percent_of_method_compile > expected_method_compile_coverage || 
          phase_subtotal < minimum_meaningful_method_compile, 
          "Must account for method compilation");

  if( Phase::_t_temporaryTimer1.seconds() > minimum_reported_time ) {
    tty->cr();
    tty->print_cr ("    temporaryTimer1: %3.3f sec", Phase::_t_temporaryTimer1.seconds());
  }
  if( Phase::_t_temporaryTimer2.seconds() > minimum_reported_time ) {
    tty->cr();
    tty->print_cr ("    temporaryTimer2: %3.3f sec", Phase::_t_temporaryTimer2.seconds());
  }
  tty->print_cr ("    output    : %3.3f sec", Phase::_t_output.seconds());
  tty->print_cr ("      isched    : %3.3f sec", Phase::_t_instrSched.seconds());
  tty->print_cr ("      bldOopMaps: %3.3f sec", Phase::_t_buildOopMaps.seconds());
}
#endif
