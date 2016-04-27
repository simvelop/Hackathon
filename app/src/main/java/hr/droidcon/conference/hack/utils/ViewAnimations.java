package hr.droidcon.conference.hack.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * Easily expand or collapse a view.
 * @author Arnaud Camus
 */
public class ViewAnimations {
    public static void expand(final View v) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translation = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);

        v.setVisibility(View.VISIBLE);

        translation.setDuration(500);
        translation.setInterpolator(new AccelerateInterpolator());
        alpha.setDuration(200);
        alpha.setStartOffset(300);
        alpha.setInterpolator(new AccelerateInterpolator());

        animationSet.addAnimation(translation);
        animationSet.addAnimation(alpha);
        v.startAnimation(animationSet);
    }

    public static void collapse(final View v) {
        TranslateAnimation anim = null;

        anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
        Animation.AnimationListener collapselistener= new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }
        };

        anim.setAnimationListener(collapselistener);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }
}

