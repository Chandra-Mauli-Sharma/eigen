package eigen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eigen.TokenType.*;

public class Lexer {

    private String charStream;
    List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Lexer(String charStream) {
        this.charStream = charStream;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
        case '(':
            addToken(LEFT_PAREN);
            break;
        case ')':
            addToken(RIGHT_PAREN);
            break;
        case '{':
            addToken(LEFT_BRACE);
            break;
        case '}':
            addToken(RIGHT_BRACE);
            break;
        case ',':
            addToken(COMMA);
            break;
        case '.':
            addToken(DOT);
            break;
        case '-':
            addToken(MINUS);
            break;
        case '+':
            addToken(PLUS);
            break;
        case ';':
            addToken(SEMICOLON);
            break;
        case '*':
            addToken(STAR);
            break;
        case ':':
            addToken(COLON);
            break;
        case '!':
            addToken(match('=') ? BANG_EQUAL : BANG);
            break;
        case '=':
            addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '/':
            if (match('/'))
                while (peek() != '\n')
                    advance();
            else
                addToken(SLASH);
            break;
        case ' ':
        case '\r':
        case '\t':
            break;
        case '\n':
            line++;
            break;
        case '"':
            string();
            break;
        case '|':
            addToken(match('|') ? OR : BITWISE_OR);
            break;
        case '&':
            addToken(match('&') ? AND : BITWISE_AND);
            break;
        default:
            if (Character.isDigit(c))
                number();
            else if (Character.isLetter(c))
                identifier();
            else
                Eigen.error(line, "Unexpected character");
            break;
        }
    }

    private void identifier() {
        while (Character.isLetterOrDigit(peek()))
            advance();

        var testText = charStream.substring(start, current);
        var type = keywords.get(testText);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (Character.isDigit(peek()))
            advance();

        if (peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            while (Character.isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(charStream.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= charStream.length())
            return '\0';
        return charStream.charAt(current + 1);
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return charStream.charAt(current);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Eigen.error(line, "\" required");
        }

        advance();

        var value = charStream.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char c) {
        if (isAtEnd())
            return false;
        if (charStream.charAt(current) != c)
            return false;

        current++;
        return true;
    }

    private char advance() {
        return charStream.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        var text = charStream.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= charStream.length();
    }

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("class", CLASS);
        keywords.put("function", FUN);
        keywords.put("var", VAR);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("while", WHILE);
        keywords.put("null", NULL);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
    }
}
