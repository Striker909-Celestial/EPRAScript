package com.epra.eprascript.basic_parsers;

/// A [Parser] subclass that parses for a specific [Character].
///
/// The parser function will find the first instance of the character
/// or fail if that character is not found.
///
/// @author Striker-909
/// @since v0.0.0
public class CharParser extends Parser<Character>{

    /// A [Parser] subclass that parses for a specific [Character].
    ///
    /// The parser function will find the first instance of the character
    /// or fail if that character is not found.
    /// @param c The character to parse for
    public CharParser(char c) {
        super(s -> {
            int i = s.indexOf(c);
            return new Token<>(c, (i >= 0) ? s.substring(0, i) : "", (i < s.length() - 1) ? s.substring(i + 1) : "", i != -1);
        });
    }
}
