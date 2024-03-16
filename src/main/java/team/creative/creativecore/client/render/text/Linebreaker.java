package team.creative.creativecore.client.render.text;

import java.util.function.Predicate;

public enum Linebreaker {
    
    WHITESPACE(' '),
    DOT('.', true, true),
    SLASH('/', true, false),
    BACKSLASH('\\', true, false),
    DASH('-', true, true),
    UPPERCASE(Character::isUpperCase, true, false);
    
    public final Predicate<Character> predicate;
    public final boolean includeChar;
    public final boolean head;
    
    Linebreaker(char character) {
        this(x -> x == character);
    }
    
    Linebreaker(Predicate<Character> predicate) {
        this(predicate, false, false);
    }
    
    Linebreaker(char character, boolean includeChar, boolean head) {
        this(x -> x == character, includeChar, head);
    }
    
    Linebreaker(Predicate<Character> predicate, boolean includeChar, boolean head) {
        this.predicate = predicate;
        this.includeChar = includeChar;
        this.head = head;
    }
    
}
