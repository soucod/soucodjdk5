#ifdef USE_PRAGMA_IDENT_HDR
#pragma ident "@(#)loaderConstraints.hpp	1.4 03/12/23 16:41:19 JVM"
#endif
/*
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

class LoaderConstraintEntry;

class LoaderConstraintTable : public Hashtable {
  friend class VMStructs;
private:

  enum Constants {
    _loader_constraint_size = 107,                     // number of entries in constraint table
    _nof_buckets            = 1009                     // number of buckets in hash table
  };

  LoaderConstraintEntry** find_loader_constraint(symbolHandle name,
                                                 Handle loader);

public:

  LoaderConstraintTable(int nof_buckets);

  LoaderConstraintEntry* new_entry(unsigned int hash, symbolOop name,
                                   klassOop klass, int num_loaders,
                                   int max_loaders);

  LoaderConstraintEntry* bucket(int i) {
    return (LoaderConstraintEntry*)Hashtable::bucket(i);
  }

  LoaderConstraintEntry** bucket_addr(int i) {
    return (LoaderConstraintEntry**)Hashtable::bucket_addr(i);
  }

  // GC support
  void oops_do(OopClosure* f);
  void always_strong_classes_do(OopClosure* blk);

  // Check class loader constraints
  bool add_entry(symbolHandle name, klassOop klass1, Handle loader1,
                                    klassOop klass2, Handle loader2, TRAPS);

  void check_signature_loaders(symbolHandle signature, Handle loader1,
                               Handle loader2, bool is_method, TRAPS);

  klassOop find_constrained_klass(symbolHandle name, Handle loader);


  // Class loader constraints

  void ensure_loader_constraint_capacity(LoaderConstraintEntry *p, int nfree);
  void extend_loader_constraint(LoaderConstraintEntry* p, Handle loader,
                                klassOop klass);
  void merge_loader_constraints(LoaderConstraintEntry** pp1,
                                LoaderConstraintEntry** pp2, klassOop klass);

  const char* check_or_update(instanceKlassHandle k, Handle loader,
                              symbolHandle name);

  
  void purge_loader_constraints(BoolObjectClosure* is_alive);

#ifndef PRODUCT
  void verify(Dictionary* dictionary);
#endif
};

class LoaderConstraintEntry : public HashtableEntry {
  friend class VMStructs;
private:
  symbolOop              _name;                   // class name
  int                    _num_loaders;
  int                    _max_loaders;
  oop*                   _loaders;                // initiating loaders

public:

  klassOop klass() { return (klassOop)literal(); }
  klassOop* klass_addr() { return (klassOop*)literal_addr(); }
  void set_klass(klassOop k) { set_literal(k); }

  LoaderConstraintEntry* next() {
    return (LoaderConstraintEntry*)HashtableEntry::next();
  }

  LoaderConstraintEntry** next_addr() {
    return (LoaderConstraintEntry**)HashtableEntry::next_addr();
  }
  void set_next(LoaderConstraintEntry* next) {
    HashtableEntry::set_next(next);
  }

  symbolOop name() { return _name; }
  symbolOop* name_addr() { return &_name; }
  void set_name(symbolOop name) { _name = name; }

  int num_loaders() { return _num_loaders; }
  void set_num_loaders(int i) { _num_loaders = i; }

  int max_loaders() { return _max_loaders; }
  void set_max_loaders(int i) { _max_loaders = i; }

  oop* loaders() { return _loaders; }
  void set_loaders(oop* loaders) { _loaders = loaders; }

  oop loader(int i) { return _loaders[i]; }
  oop* loader_addr(int i) { return &_loaders[i]; }
  void set_loader(int i, oop p) { _loaders[i] = p; }

};
