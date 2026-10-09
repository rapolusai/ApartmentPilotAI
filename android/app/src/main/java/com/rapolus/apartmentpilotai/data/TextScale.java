package com.rapolus.apartmentpilotai.data;

/**
 * Text sizes follow the reference on phones. On tablets (600dp and wider) small text is a step larger
 * so it reads comfortably at arm's length; large display text is left as designed.
 */
public final class TextScale {
    private TextScale() {}
    public static float size(float sp,int screenWidthDp) {
        if(!TabletPolicy.split(screenWidthDp))return sp;
        if(sp<=13)return sp+2;
        if(sp<=18)return sp+1;
        return sp;
    }
}
