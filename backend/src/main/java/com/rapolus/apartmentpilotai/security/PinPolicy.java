package com.rapolus.apartmentpilotai.security;
import java.util.Set;
/** One PIN rule for register, join and change-PIN: exactly six digits and not trivially guessable. Login still accepts
 * legacy 4-5 digit PINs; the login response then carries pinChangeRequired=true. */
public final class PinPolicy {
    public static final String LENGTH_MESSAGE="Use a six-digit PIN.";
    public static final String WEAK_MESSAGE="Choose a harder PIN. Avoid repeated digits, sequences such as 123456 and patterns such as 121212.";
    private static final Set<String> COMMON=Set.of("121212","112233","123123","123321","111222","110011","101010","010101","696969","131313","007007","100100","200200","998877","778899","147258","159753","142536","102030");
    private PinPolicy() {
    }
    public static boolean weak(String pin) {
        if(pin==null||!pin.matches("[0-9]{4,6}"))return true;
        if(pin.chars().distinct().count()==1)return true;
        boolean up=true,down=true;
        for(int i=1; i<pin.length(); i++) {
            int step=pin.charAt(i)-pin.charAt(i-1);
            up&=step==1;
            down&=step==-1;
        }
        return up||down||COMMON.contains(pin);
    }
    /** True when a PIN that just signed in should be replaced: shorter than six digits or weak. */
    public static boolean changeRequired(String pin) {
        return pin==null||pin.length()!=6||weak(pin);
    }
    /** Throws IllegalArgumentException (400 with the message) when a new PIN breaks the rule. */
    public static String requireNew(String pin) {
        if(pin==null||!pin.matches("[0-9]{6}"))throw new IllegalArgumentException(LENGTH_MESSAGE);
        if(weak(pin))throw new IllegalArgumentException(WEAK_MESSAGE);
        return pin;
    }
}
