package com.epra.eprascript.parsers.function;

import com.epra.eprascript.parsers.Parser;
import com.epra.eprascript.parsers.alphanumeric.RegExParser;
import com.epra.eprascript.parsers.Token;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/// A [Parser] subclass that parses for a specific function signature and applies a function
/// to the values specified in the signature.
/// @param <T> The return type of this function parser
/// @author Striker-909
/// @since v0.2.0
public class FunctionParser<T> extends Parser<Supplier<T>> {
    /// The [HashMap] of all string addresses and corresponding [Suppliers][Supplier].
    private static final HashMap<String, Supplier> ADDRESSES = new HashMap<>();

    /// Registers a [Supplier] at an address that can be fetched using ([FunctionParser#FUNCTION_FETCHER]).
    ///
    /// The address is found by taking the `toString` of the [Supplier] after the `/`,
    /// and appending `§` to either side,
    /// resulting in an address of the form `§.x................@........§`
    /// where `.` represents a number or lowercase letter.
    /// In some instances, there will be only 7 characters after the `@` instead of 8.
    ///
    /// Will fail if there is already a [Supplier] at the same address.
    /// @param supplier The supplier to register
    /// @return If the supplier was registered successfully
    private static boolean register(Supplier<?> supplier) {
        String address = supplierToAddress(supplier);
        if (ADDRESSES.containsKey(address)) return false;
        ADDRESSES.put(address, supplier);
        return true;
    }

    /// Finds the address of a given [Supplier].
    ///
    /// The address is found by taking the `toString` of the [Supplier] after the `/`,
    /// and appending `§` to either side,
    /// resulting in an address of the form `§.x................@........§`
    /// where `.` represents a number or lowercase letter.
    /// In some instances, there will be only 7 characters after the `@` instead of 8.
    /// @param supplier A supplier
    /// @return The address of that supplier
    public static String supplierToAddress(Supplier<?> supplier) {
        return "§" + supplier.toString().split("/")[1] + "§";
    }
    /// Finds the address of a given [Supplier].
    ///
    /// The address is found by taking the `toString` of the [Supplier] after the `/`,
    /// and appending `§` to either side,
    /// resulting in an address of the form `§.x................@........§`
    /// where `.` represents a number or lowercase letter.
    /// In some instances, there will be only 7 characters after the `@` instead of 8.
    /// @param s A supplier
    /// @return The address of that supplier
    public static <S> String supplierToAddress(S s) {
        return "§" + s.toString().split("/")[1] + "§";
    }

    /// A [Parser] that identifies the first instance of a function address in a
    /// string and replaces the address with the value of that supplier if it is
    /// a registered address.
    /// @see FunctionParser#register(Supplier)
    public static final Parser<Supplier<?>> FUNCTION_FETCHER = new Parser<>(
            s -> {
                Token<String> t = new RegExParser("§[\\da-z]x[\\da-z]{16}@[\\da-z]+§").parse(s);
                if (!t.success()) { return new Token<>(null, "", s, false); }
                return new Token<Supplier<?>>(ADDRESSES.get(t.value()), t.head(), t.follow(), true);
            }
    );

    /// A [HashMap] of all function signatures and the corresponding [RegExParser] for that signature.
    private static final HashMap<String, RegExParser> SIGNATURES = new HashMap<>();
    /// A [RegExParser] for the standard variable signature.
    ///
    /// The standard variable signature is `$NAME$` where `NAME` is a letter or underscore followed by any number of letters,
    /// digits, or underscores.
    public static final RegExParser STANDARD_VARIABLE_REGEX = new RegExParser("\\\\\\$[a-zA-Z_][a-zA-Z_\\d]*\\\\\\$");

    /// A [Parser] subclass that parses for a specific function signature and applies a function
    /// to the values specified in the signature.
    ///
    /// Parsing will return a [Supplier] that will return the result of the function.
    /// Parsed suppliers are stored in [FunctionParser#ADDRESSES] with their address given by
    /// [FunctionParser#supplierToAddress].
    ///
    /// Variable names in the signature should match the names of keys in a [HashMap] that the
    /// function will use.
    ///
    /// Using [Parser#recursiveReplaceAll(String)] is recommended for fully parsing a layered functional statement.
    ///
    /// @param signature The signature of the function
    /// @param valueRegex A RegEx expression that specifies what values are
    /// permitted as a parameter input for the function
    /// @param variableParser A parser for variable names in the function
    /// @param function A function from a hash map of variable name-value pairs to
    /// a supplier of a value of type `T`
    public FunctionParser(String signature, String valueRegex, Parser<String> variableParser, Function<HashMap<String, String>, Supplier<T>> function) {
        super(
                s -> {
                    RegExParser regex = SIGNATURES.get(signature);
                    Token<String> t = regex.parse(s);
                    if (!t.success()) { return new Token<>(null, "", s, false); }
                    HashMap<String, String> variables = regex.getNamedGroups(s).value();
                    variables.forEach((k, v) -> {
                        Token<Supplier<?>> supplierToken = FUNCTION_FETCHER.parse(v);
                        if (supplierToken.success()) {
                            variables.put(k, supplierToken.value().get().toString());
                        }
                    });
                    Supplier<T> supplier = function.apply(variables);
                    register(supplier);
                    return new Token<>(supplier, t.head(), t.follow(), true);
                }
        );
        String signatureRegex = variableParser.replaceAll(
                RegExParser.REGEX_META_CHARACTERS.replaceAll(signature, c -> "\\" + c),
                value -> "(?<" + value + ">(?:§[\\da-z]x[\\da-z]{16}@[\\da-z]+§|" + valueRegex + "))");
        RegExParser signatureRegexParser = new RegExParser(signatureRegex);
        SIGNATURES.put(signature, signatureRegexParser);
    }
    /// A [Parser] subclass that parses for a specific function signature and applies a function
    /// to the values specified in the signature.
    ///
    /// Parsing will return a [Supplier] that will return the result of the function.
    /// Parsed suppliers are stored in [FunctionParser#ADDRESSES] with their address given by
    /// [FunctionParser#supplierToAddress].
    ///
    /// Variable names in the signature should match the names of keys in a [HashMap] that the
    /// function will use.
    ///
    /// Using [Parser#recursiveReplaceAll(String)] is recommended for fully parsing a layered functional statement.
    ///
    /// Uses [FunctionParser#STANDARD_VARIABLE_REGEX] as the variable [Parser].
    ///
    /// @param signature The signature of the function
    /// @param valueRegex A RegEx expression that specifies what values are
    /// permitted as a parameter input for the function
    /// @param function A function from a hash map of variable name-value pairs to
    /// a supplier of a value of type `T`
    public FunctionParser(String signature, String valueRegex, Function<HashMap<String, String>, Supplier<T>> function) {
        this(signature, valueRegex, STANDARD_VARIABLE_REGEX, function);
    }

    /// A [Parser] subclass that parses for a specific function signature and applies a function
    /// to the values specified in the signature.
    ///
    /// Parsing will return a [Supplier] that will return the result of the function.
    /// Parsed suppliers are stored in [FunctionParser#ADDRESSES] with their address given by
    /// [FunctionParser#supplierToAddress].
    ///
    /// Variable names in the signature should match the names of keys in a [HashMap] that the
    /// function will use.
    ///
    /// Using [Parser#recursiveReplaceAll(String)] is recommended for fully parsing a layered functional statement.
    ///
    /// @param signature The signature of the function
    /// @param valueRegex A hash map from variable names to RegEx expressions specifying what values are permitted
    /// as a parameter input for that variable
    /// @param variableParser A parser for variable names in the function
    /// @param function A function from a hash map of variable name-value pairs to
    /// a supplier of a value of type `T`
    public FunctionParser(String signature, HashMap<String, String> valueRegex, Parser<String> variableParser, Function<HashMap<String, String>, Supplier<T>> function) {
        super(
                s -> {
                    RegExParser regex = SIGNATURES.get(signature);
                    Token<String> t = regex.parse(s);
                    if (!t.success()) { return new Token<>(null, "", s, false); }
                    HashMap<String, String> variables = regex.getNamedGroups(s).value();
                    variables.forEach((k, v) -> {
                        Token<Supplier<?>> supplierToken = FUNCTION_FETCHER.parse(v);
                        if (supplierToken.success()) {
                            variables.put(k, supplierToken.value().get().toString());
                        }
                    });
                    Supplier<T> supplier = function.apply(variables);
                    register(supplier);
                    return new Token<>(supplier, t.head(), t.follow(), true);
                }
        );
        String signatureRegex = variableParser.replaceAll(
                RegExParser.REGEX_META_CHARACTERS.replaceAll(signature, c -> "\\" + c),
                value -> "(?<" + value + ">(?:§[\\da-z]x[\\da-z]{16}@[\\da-z]+§|" + valueRegex.get(value) + "))");
        RegExParser signatureRegexParser = new RegExParser(signatureRegex);
        SIGNATURES.put(signature, signatureRegexParser);
    }

    /// A [Parser] subclass that parses for a specific function signature and applies a function
    /// to the values specified in the signature.
    ///
    /// Parsing will return a [Supplier] that will return the result of the function.
    /// Parsed suppliers are stored in [FunctionParser#ADDRESSES] with their address given by
    /// [FunctionParser#supplierToAddress].
    ///
    /// Variable names in the signature should match the names of keys in a [HashMap] that the
    /// function will use.
    ///
    /// Using [Parser#recursiveReplaceAll(String)] is recommended for fully parsing a layered functional statement.
    ///
    /// Uses [FunctionParser#STANDARD_VARIABLE_REGEX] as the variable [Parser].
    ///
    /// @param signature The signature of the function
    /// @param valueRegex A hash map from variable names to RegEx expressions specifying what values are permitted
    /// as a parameter input for that variable
    /// @param function A function from a hash map of variable name-value pairs to
    /// a supplier of a value of type `T`
    public FunctionParser(String signature, HashMap<String, String> valueRegex, Function<HashMap<String, String>, Supplier<T>> function) {
        this(signature, valueRegex, STANDARD_VARIABLE_REGEX, function);
    }

    /// Replaces the first success of the parser when parsing the input with the address of the parsed [Supplier].
    /// @param input The string to be parsed
    /// @return The string with the first parser success replaced with the address of the parsed supplier
    public String replace(String input) {
        return replace(input, s -> "§" + s.toString().split("/")[1] + "§");
    }
}