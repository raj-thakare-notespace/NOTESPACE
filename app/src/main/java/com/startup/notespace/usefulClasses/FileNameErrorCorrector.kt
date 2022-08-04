package com.startup.notespace.usefulClasses

class FileNameErrorCorrector {

    fun correctErrors( s : String) : String {
        s.replace(".","_")
        s.replace("$","_dol_")
        s.replace("[","(")
        s.replace("]",")")
        s.replace("#","_hash_")
        s.replace("/","_")
        return s
    }
}