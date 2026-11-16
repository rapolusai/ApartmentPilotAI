package com.rapolus.apartmentpilotai.ui;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.rapolus.apartmentpilotai.R;
/**
 * Setup guidance: dims the screen and lights up the next button with a softly pulsing circle and a short tip.
 * It only highlights; the person taps the real button, so they learn the real screen. Native, no library.
 */
public final class Spotlight extends FrameLayout {
    public interface Done {
        void dismissed();
    }
    private final Paint dim=new Paint(Paint.ANTI_ALIAS_FLAG),clear=new Paint(Paint.ANTI_ALIAS_FLAG),ring=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final View target;
    private final Done done;
    private float cx=-1,cy=-1,radius=0,pulse=0;
    private ValueAnimator animator;
    private Spotlight(Activity activity,View target,String tip,Done done) {
        super(activity);
        this.target=target;
        this.done=done;
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        setClickable(true);
        dim.setColor(0xB3101A17);
        clear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ring.setStyle(Paint.Style.STROKE);
        ring.setColor(0xFFDFEFA7);
        Ui ui=new Ui(activity);
        ring.setStrokeWidth(ui.dp(3));
        LinearLayout bubble=ui.column();
        bubble.setPadding(ui.dp(18),ui.dp(16),ui.dp(18),ui.dp(12));
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(ui.color(R.color.ap_surface));
        bg.setCornerRadius(ui.dp(18));
        bg.setStroke(ui.dp(4),ui.color(R.color.ap_lime));
        bubble.setBackground(bg);
        bubble.addView(ui.text("YOUR NEXT STEP",11,true,R.color.ap_pine));
        ui.gap(bubble,4);
        TextView text=ui.text(tip,16,true,R.color.ap_ink);
        bubble.addView(text);
        LinearLayout actions=ui.row();
        ui.button(actions,"Got it",R.drawable.ic_check,true,this::dismiss).setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
        bubble.addView(actions);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-1,-2);
        lp.leftMargin=lp.rightMargin=ui.dp(20);
        lp.gravity=Gravity.CENTER;
        addView(bubble,lp);
        setContentDescription(tip);
        setOnTouchListener((v,e)-> {
            if(e.getAction()==MotionEvent.ACTION_UP&&target!=null&&cx>=0) {
                float dx=e.getX()-cx,dy=e.getY()-cy;
                if(dx*dx+dy*dy<=radius*radius) {
                    dismiss();
                    target.performClick();
                }
            }
            return true;
        });
        post(()-> {
            place(bubble,ui);
            announceForAccessibility(tip);
        });
    }
    private void place(LinearLayout bubble,Ui ui) {
        if(target==null||!target.isShown())return;
        int[] me=new int[2],it=new int[2];
        getLocationOnScreen(me);
        target.getLocationOnScreen(it);
        cx=it[0]-me[0]+target.getWidth()/2f;
        cy=it[1]-me[1]+target.getHeight()/2f;
        radius=Math.max(target.getWidth(),target.getHeight())/2f+ui.dp(10);
        radius=Math.min(radius,Math.min(getWidth(),getHeight())/2f);
        FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)bubble.getLayoutParams();
        lp.gravity=Gravity.TOP;
        bubble.measure(MeasureSpec.makeMeasureSpec(getWidth()-ui.dp(40),MeasureSpec.EXACTLY),MeasureSpec.UNSPECIFIED);
        int h=bubble.getMeasuredHeight();
        lp.topMargin=(int)(cy+radius+ui.dp(16)+h<getHeight()?cy+radius+ui.dp(16):Math.max(ui.dp(16),cy-radius-ui.dp(16)-h));
        bubble.setLayoutParams(lp);
        if(ValueAnimator.areAnimatorsEnabled()) {
            animator=ValueAnimator.ofFloat(0,1);
            animator.setDuration(1200);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.addUpdateListener(a-> {
                pulse=(float)a.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }
        invalidate();
    }
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,0,getWidth(),getHeight(),dim);
        if(cx>=0) {
            canvas.drawCircle(cx,cy,radius,clear);
            ring.setAlpha((int)(255*(1f-pulse*0.6f)));
            canvas.drawCircle(cx,cy,radius+pulse*radius*0.18f,ring);
        }
        super.onDraw(canvas);
    }
    public void dismiss() {
        if(animator!=null)animator.cancel();
        ViewParent parent=getParent();
        if(parent instanceof ViewGroup)((ViewGroup)parent).removeView(this);
        if(done!=null)done.dismissed();
    }
    /** Shows a spotlight on target (or a centred tip when target is null). Returns the overlay. */
    public static Spotlight show(Activity activity,View target,String tip,Done done) {
        ViewGroup decor=(ViewGroup)activity.getWindow().getDecorView();
        for(int i=decor.getChildCount()-1; i>=0; i--)if(decor.getChildAt(i) instanceof Spotlight)((Spotlight)decor.getChildAt(i)).dismiss();
        Spotlight s=new Spotlight(activity,target,tip,done);
        decor.addView(s,new ViewGroup.LayoutParams(-1,-1));
        return s;
    }
    /** Finds the first visible view showing exactly this text (a button label), or null. */
    public static View findByText(View root,String text) {
        if(text==null||text.isEmpty()||root==null||!root.isShown())return null;
        if(root instanceof TextView&&text.equalsIgnoreCase(((TextView)root).getText().toString().trim()))return root;
        if(root instanceof ViewGroup) {
            ViewGroup g=(ViewGroup)root;
            for(int i=0; i<g.getChildCount(); i++) {
                View found=findByText(g.getChildAt(i),text);
                if(found!=null)return found;
            }
        }
        return null;
    }
}
