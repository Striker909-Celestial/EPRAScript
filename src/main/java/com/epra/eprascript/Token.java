package com.epra.eprascript;

/// The output produced by a [Parser].
///
/// Queer Coded by Striker-909.
/// If you use this class or a method from this class in its entirety, please make sure to give credit.
/// @param value The value returned by the parser
/// @param head The section of the string before the parsed section
/// @param follow The section of the spring after the parsed section
/// @param success If the parse was successful
public record Token<T> (T value, String head, String follow, boolean success){

    public String toString(){
        if (!success){ return "(success: false)"; }
        return "(value: " + value.toString() + ", head: " + head + ", follow: " + follow + ", success: true)";
    }
}
