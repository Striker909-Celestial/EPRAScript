package com.epra.eprascript.parsers.function;

import com.epra.eprascript.parsers.Parser;
import com.epra.eprascript.parsers.Token;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Supplier;
/// A [FunctionParser] for assigning values to variables that can then be used later.
///
/// **Currently highly unstable.**
/// @author Striker-909
/// @since v0.2.1
public class AssignmentParser<T> extends FunctionParser<FunctionParser<T>>{
    /// A [TreeMap] from all variable names (strings) to the corresponding [FunctionParser] for parsing that
    /// variable.
    ///
    /// Ordered by variable name length, from longest to shortest.
    private static final TreeMap<String, FunctionParser> ASSIGNMENTS = new TreeMap<>(Comparator.reverseOrder());
    /// A [Parser] that fetches assigned variables by running through all parsers in [AssignmentParser#ASSIGNMENTS].
    ///
    /// The parse method will return the output of the successful parser if there is a successful parser
    /// and fails otherwise.
    @SuppressWarnings("unchecked")
    public static final Parser<?> ASSIGNMENT_FETCHER = new Parser<>(
            s -> {
                for (String key : ASSIGNMENTS.keySet()) {
                    Token<Supplier<?>> token = ASSIGNMENTS.get(key).parse(s);
                    if (token.success()) {
                        return new Token<>(token.value(), token.head(), token.follow(), true);
                    }
                }
                return new Token<>(null, "", s, false);
            }
    );
    /// A [FunctionParser] for assigning values to variables that can then be used later.
    ///
    /// **Currently highly unstable.**
    /// @param valueRegex A RegEx expression that specifies what values are
    /// permitted as a value of a variable assignment
    /// @param valueParser A parser to parse the value of a variable assignment
    public AssignmentParser(String valueRegex, Parser<T> valueParser) {
        super(
                "$name$=$value$",
                new HashMap<String, String>() {{
                    put("name", "[a-zA-Z_][a-zA-Z_\\d]*");
                    put("value", valueRegex);
                }},
                new Parser<>(s -> {
                    Token<String> token = FunctionParser.STANDARD_VARIABLE_REGEX.parse(s);
                    if (!token.success()) { return token; }
                    return new Token<>(token.value().substring(2, token.value().length() - 2), token.head(), token.follow(), true);
                }),
                (vals) -> {
                    Supplier<T> supplier = () -> valueParser.parse(vals.get("value")).value();
                    // System.out.println(supplier);
                    FunctionParser<T> fpOut = new FunctionParser<>(
                            vals.get("name"),
                            "",
                            (hm) -> supplier
                    );
                    ASSIGNMENTS.put(vals.get("name"), fpOut);
                    return () -> fpOut;
                }
        );
    }
}
