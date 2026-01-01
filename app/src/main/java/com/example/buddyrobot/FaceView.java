// FaceView - Enhanced Natural Feminine Style (Robot Emo)
// With Smooth Transitions, Micro-expressions, and Advanced Animations
package com.example.buddyrobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.os.Handler;

public class FaceView extends View {

    private Paint paint;
    private Paint accentPaint;
    private Paint bgPaint;

    // Expression Management
    private String currentExpression = "NEUTRAL";
    private String targetExpression = "NEUTRAL";
    private float transitionProgress = 1f; // 0 to 1 (transition complete at 1)
    private boolean isTransitioning = false;

    // Blink Animation
    private float blinkProgress = 0f;
    private boolean isBlinking = false;

    // Eye positions and properties
    private float leftEyeX, leftEyeY;
    private float rightEyeX, rightEyeY;
    private float eyeSize = 80f;

    // Animation variables
    private float animProgress = 0f;
    private long animStartTime = 0;

    // Micro-expression variables (NEW!)
    private float gazeX = 0f;
    private float gazeY = 0f;
    private float eyeDartX = 0f;
    private float eyeDartY = 0f;
    private long lastGazeChange = 0;
    private long lastDartTime = 0;

    // Expression-specific animation variables
    private float bounceOffset = 0f;
    private float shakeOffset = 0f;
    private float pulseScale = 1f;
    private float wiggleAngle = 0f;
    private float floatOffset = 0f;
    private float sparklePhase = 0f;
    private float teardropY = 0f;
    private float squashStretch = 1f;
    private float eyeWobble = 0f;

    // Idle behavior variables (NEW!)
    private long idleStartTime = 0;
    private float idleHeadTilt = 0f;
    private float idleBobbing = 0f;

    // Pink color theme
    private int primaryColor = 0xFFFF6B9D;
    private int accentColor = 0xFFFFB3D9;
    private int darkColor = 0xFFE85D8A;

    // Easing functions helper
    private static final float PI = (float) Math.PI;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(primaryColor);

        accentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setColor(accentColor);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);

        startBlinkAnimation();
        startContinuousAnimation();
        startMicroExpressions();

        idleStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        leftEyeX = width * 0.35f;
        leftEyeY = height * 0.4f;
        rightEyeX = width * 0.65f;
        rightEyeY = height * 0.4f;

        // Apply idle animations when no specific emotion
        applyIdleBehavior();

        // Handle transition between expressions
        if (isTransitioning && transitionProgress < 1f) {
            // Draw blended expression during transition
            canvas.save();
            canvas.scale(1f, 1f, width / 2f, height / 2f);
            drawCurrentExpression(canvas);
            canvas.restore();
        } else {
            drawCurrentExpression(canvas);
        }
    }

    private void drawCurrentExpression(Canvas canvas) {
        switch (currentExpression.toUpperCase()) {
            case "HAPPY": drawHappyFace(canvas); break;
            case "SAD": drawSadFace(canvas); break;
            case "SURPRISE":
            case "SURPRISED": drawSurpriseFace(canvas); break;
            case "ANGRY": drawAngryFace(canvas); break;
            case "LOVE": drawLoveFace(canvas); break;
            case "SLEEPY": drawSleepyFace(canvas); break;
            case "EXCITED": drawExcitedFace(canvas); break;
            case "CONFUSED": drawConfusedFace(canvas); break;
            case "COOL": drawCoolFace(canvas); break;
            case "SHY": drawShyFace(canvas); break;
            case "THINK":
            case "THINKING": drawThinkingFace(canvas); break;
            case "LAUGH":
            case "LAUGHING": drawLaughingFace(canvas); break;
            default: drawNeutralFace(canvas); break;
        }
    }

    // ========== IDLE BEHAVIOR (NEW!) ==========
    private void applyIdleBehavior() {
        long idleTime = System.currentTimeMillis() - idleStartTime;

        // Gentle head bobbing every 3 seconds
        idleBobbing = (float) Math.sin(idleTime / 3000.0 * PI) * 2f;

        // Occasional small head tilt
        if (idleTime % 5000 < 100) {
            idleHeadTilt = ((float) Math.random() - 0.5f) * 4f;
        }
    }

    // ========== MICRO-EXPRESSIONS (NEW!) ==========
    private void startMicroExpressions() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                // Random eye darts (saccades) - every 2-5 seconds
                if (currentTime - lastDartTime > 2000 + (long)(Math.random() * 3000)) {
                    eyeDartX = ((float) Math.random() - 0.5f) * 8f;
                    eyeDartY = ((float) Math.random() - 0.5f) * 6f;
                    lastDartTime = currentTime;
                }

                // Smooth gaze changes - every 4-8 seconds
                if (currentTime - lastGazeChange > 4000 + (long)(Math.random() * 4000)) {
                    gazeX = ((float) Math.random() - 0.5f) * eyeSize * 0.3f;
                    gazeY = ((float) Math.random() - 0.5f) * eyeSize * 0.3f;
                    lastGazeChange = currentTime;
                }

                // Smooth interpolation for gaze
                gazeX *= 0.95f; // Decay to center
                gazeY *= 0.95f;
                eyeDartX *= 0.9f; // Quick decay
                eyeDartY *= 0.9f;

                handler.postDelayed(this, 50);
            }
        });
    }

    // ========== EASING FUNCTIONS (NEW!) ==========
    private float easeInOutQuad(float t) {
        return t < 0.5f ? 2f * t * t : -1f + (4f - 2f * t) * t;
    }

    private float easeOutElastic(float t) {
        float p = 0.3f;
        return (float) (Math.pow(2f, -10f * t) * Math.sin((t - p / 4f) * (2f * PI) / p) + 1f);
    }

    private float easeOutBounce(float t) {
        if (t < 1f / 2.75f) {
            return 7.5625f * t * t;
        } else if (t < 2f / 2.75f) {
            t -= 1.5f / 2.75f;
            return 7.5625f * t * t + 0.75f;
        } else if (t < 2.5f / 2.75f) {
            t -= 2.25f / 2.75f;
            return 7.5625f * t * t + 0.9375f;
        } else {
            t -= 2.625f / 2.75f;
            return 7.5625f * t * t + 0.984375f;
        }
    }

    // ========== EXPRESSION DRAWINGS WITH ENHANCED ANIMATIONS ==========

    private void drawNeutralFace(Canvas canvas) {
        // Enhanced breathing with easing
        float breathPhase = animProgress * PI * 2;
        float breathEase = (1f - (float) Math.cos(breathPhase)) * 0.5f;
        float breathScale = 1f + breathEase * 0.012f; // Subtle
        float breathY = (float) Math.sin(breathPhase) * 1.5f;

        canvas.save();
        canvas.translate(0, breathY + idleBobbing);
        canvas.rotate(idleHeadTilt, getWidth() / 2f, getHeight() / 2f);
        canvas.scale(breathScale, breathScale, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);
        float eyeHeight = eyeSize * (1 - blinkProgress);

        canvas.drawOval(leftEyeX - eyeSize/2, leftEyeY - eyeHeight/2,
                leftEyeX + eyeSize/2, leftEyeY + eyeHeight/2, paint);
        canvas.drawOval(rightEyeX - eyeSize/2, rightEyeY - eyeHeight/2,
                rightEyeX + eyeSize/2, rightEyeY + eyeHeight/2, paint);

        if (blinkProgress < 0.5f) {
            accentPaint.setColor(Color.WHITE);
            float pupilSize = eyeSize * 0.3f;
            // Apply micro-movements to pupils
            canvas.drawCircle(leftEyeX + gazeX + eyeDartX, leftEyeY + gazeY + eyeDartY, pupilSize, accentPaint);
            canvas.drawCircle(rightEyeX + gazeX + eyeDartX, rightEyeY + gazeY + eyeDartY, pupilSize, accentPaint);
        }

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(getWidth() * 0.35f, getHeight() * 0.7f,
                getWidth() * 0.65f, getHeight() * 0.7f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawHappyFace(Canvas canvas) {
        // Elastic bounce with anticipation
        float bouncePhase = animProgress * PI * 2;
        bounceOffset = easeOutBounce((float) Math.abs(Math.sin(bouncePhase))) * 10f;
        float anticipation = (float) Math.sin(bouncePhase - PI / 4) * 2f;
        float gentleScale = 1f + (float) Math.sin(bouncePhase) * 0.035f;

        canvas.save();
        canvas.translate(0, -bounceOffset + anticipation);
        canvas.scale(gentleScale, gentleScale, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);
        paint.setStrokeWidth(16f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        RectF leftEyeRect = new RectF(leftEyeX - eyeSize/2.2f, leftEyeY - eyeSize/4,
                leftEyeX + eyeSize/2.2f, leftEyeY + eyeSize/3);
        canvas.drawArc(leftEyeRect, 190, 160, false, paint);

        RectF rightEyeRect = new RectF(rightEyeX - eyeSize/2.2f, rightEyeY - eyeSize/4,
                rightEyeX + eyeSize/2.2f, rightEyeY + eyeSize/3);
        canvas.drawArc(rightEyeRect, 190, 160, false, paint);

        paint.setStrokeWidth(14f);
        RectF mouthRect = new RectF(getWidth() * 0.32f, getHeight() * 0.55f,
                getWidth() * 0.68f, getHeight() * 0.78f);
        canvas.drawArc(mouthRect, 20, 140, false, paint);

        // Pulsing blush with smooth animation
        pulseScale = 1f + easeInOutQuad((float) Math.abs(Math.sin(animProgress * PI * 3))) * 0.15f;
        accentPaint.setColor(accentColor);
        accentPaint.setAlpha(140);
        canvas.drawCircle(getWidth() * 0.2f, getHeight() * 0.58f, 32f * pulseScale, accentPaint);
        canvas.drawCircle(getWidth() * 0.8f, getHeight() * 0.58f, 32f * pulseScale, accentPaint);
        accentPaint.setAlpha(255);

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawSadFace(Canvas canvas) {
        float droopOffset = (float) Math.sin(animProgress * PI) * 4f;
        float slowBreathe = (float) Math.sin(animProgress * PI * 0.5f) * 3f;

        canvas.save();
        canvas.translate(0, droopOffset + slowBreathe);

        paint.setColor(primaryColor);
        paint.setStyle(Paint.Style.FILL);

        float sadEyeSize = eyeSize * 0.75f;
        canvas.drawOval(leftEyeX - sadEyeSize/2, leftEyeY - sadEyeSize/3,
                leftEyeX + sadEyeSize/2, leftEyeY + sadEyeSize/3, paint);
        canvas.drawOval(rightEyeX - sadEyeSize/2, rightEyeY - sadEyeSize/3,
                rightEyeX + sadEyeSize/2, rightEyeY + sadEyeSize/3, paint);

        accentPaint.setColor(Color.WHITE);
        canvas.drawCircle(leftEyeX, leftEyeY + sadEyeSize/4, sadEyeSize * 0.2f, accentPaint);
        canvas.drawCircle(rightEyeX, rightEyeY + sadEyeSize/4, sadEyeSize * 0.2f, accentPaint);

        // Smooth falling tears with physics
        float tearPhase = animProgress * PI * 1.5f;
        float tearY = easeInOutQuad((float) Math.sin(tearPhase) * 0.5f + 0.5f) * 30f;
        accentPaint.setColor(accentColor);
        accentPaint.setAlpha(180);

        canvas.drawCircle(leftEyeX - eyeSize/4, leftEyeY + eyeSize/2 + tearY, 10f, accentPaint);
        canvas.drawOval(leftEyeX - eyeSize/4 - 8, leftEyeY + eyeSize/2 + tearY - 3,
                leftEyeX - eyeSize/4 + 8, leftEyeY + eyeSize/2 + 20 + tearY, accentPaint);

        accentPaint.setAlpha(255);

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF mouthRect = new RectF(getWidth() * 0.36f, getHeight() * 0.65f,
                getWidth() * 0.64f, getHeight() * 0.82f);
        canvas.drawArc(mouthRect, 15, 150, false, paint);
        paint.setStyle(Paint.Style.FILL);

        canvas.restore();
    }

    private void drawSurpriseFace(Canvas canvas) {
        // Pop effect with elastic easing
        float popPhase = animProgress * PI * 3;
        float gentlePop = 1.03f + easeOutElastic((float) Math.abs(Math.sin(popPhase))) * 0.05f;

        canvas.save();
        canvas.scale(gentlePop, gentlePop, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);
        paint.setStyle(Paint.Style.FILL);

        float wideEyeSize = eyeSize * 1.35f;
        canvas.drawCircle(leftEyeX, leftEyeY, wideEyeSize/2, paint);
        canvas.drawCircle(rightEyeX, rightEyeY, wideEyeSize/2, paint);

        accentPaint.setColor(Color.WHITE);
        float highlightSize = wideEyeSize * 0.35f;
        canvas.drawCircle(leftEyeX - wideEyeSize/6, leftEyeY - wideEyeSize/6, highlightSize, accentPaint);
        canvas.drawCircle(rightEyeX - wideEyeSize/6, rightEyeY - wideEyeSize/6, highlightSize, accentPaint);

        // Animated mouth "O"
        float mouthPulse = 1f + (float) Math.sin(animProgress * PI * 4) * 0.1f;
        paint.setStrokeWidth(13f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawCircle(getWidth() * 0.5f, getHeight() * 0.72f, 35f * mouthPulse, paint);
        paint.setStyle(Paint.Style.FILL);

        canvas.restore();
    }

    private void drawAngryFace(Canvas canvas) {
        // Intense shake with random jitter
        float shakePhase = animProgress * PI * 6;
        float gentleShake = (float) Math.sin(shakePhase) * 5f;
        float jitter = ((float) Math.random() - 0.5f) * 2f;

        canvas.save();
        canvas.translate(gentleShake + jitter, 0);

        paint.setColor(darkColor);
        paint.setStrokeWidth(14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Angry eyebrows with emphasis
        canvas.drawLine(leftEyeX - eyeSize/2.5f, leftEyeY - eyeSize/2.5f,
                leftEyeX + eyeSize/2.5f, leftEyeY - eyeSize/3.5f, paint);
        canvas.drawLine(rightEyeX - eyeSize/2.5f, rightEyeY - eyeSize/3.5f,
                rightEyeX + eyeSize/2.5f, rightEyeY - eyeSize/2.5f, paint);

        paint.setColor(primaryColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawOval(leftEyeX - eyeSize/2.5f, leftEyeY - eyeSize/4,
                leftEyeX + eyeSize/2.5f, leftEyeY + eyeSize/4, paint);
        canvas.drawOval(rightEyeX - eyeSize/2.5f, rightEyeY - eyeSize/4,
                rightEyeX + eyeSize/2.5f, rightEyeY + eyeSize/4, paint);

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        RectF mouthRect = new RectF(getWidth() * 0.35f, getHeight() * 0.62f,
                getWidth() * 0.65f, getHeight() * 0.78f);
        canvas.drawArc(mouthRect, 15, 150, false, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawLoveFace(Canvas canvas) {
        // Floating with sway
        float floatPhase = animProgress * PI * 1.5f;
        floatOffset = (float) Math.sin(floatPhase) * 10f;
        pulseScale = 1f + easeInOutQuad((float) Math.abs(Math.sin(animProgress * PI * 2.5f))) * 0.1f;
        float swayX = (float) Math.sin(animProgress * PI) * 6f;

        canvas.save();
        canvas.translate(swayX, floatOffset);

        paint.setColor(primaryColor);

        // Pulsing hearts for eyes
        canvas.save();
        canvas.scale(pulseScale, pulseScale, leftEyeX, leftEyeY);
        drawHeart(canvas, leftEyeX, leftEyeY, eyeSize * 0.7f);
        canvas.restore();

        canvas.save();
        canvas.scale(pulseScale, pulseScale, rightEyeX, rightEyeY);
        drawHeart(canvas, rightEyeX, rightEyeY, eyeSize * 0.7f);
        canvas.restore();

        paint.setStrokeWidth(14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF mouthRect = new RectF(getWidth() * 0.32f, getHeight() * 0.55f,
                getWidth() * 0.68f, getHeight() * 0.75f);
        canvas.drawArc(mouthRect, 20, 140, false, paint);

        // Orbiting hearts
        accentPaint.setColor(accentColor);
        accentPaint.setAlpha(160);

        for(int i = 0; i < 3; i++) {
            float angle = animProgress * PI * 2 + i * PI * 0.7f;
            float radius = 50f + i * 12f;
            float heartX = getWidth() * 0.5f + (float) Math.cos(angle) * radius;
            float heartY = getHeight() * 0.35f + (float) Math.sin(angle) * radius * 0.6f;
            float size = 12f + (float) Math.sin(angle) * 3f;

            drawSmallHeart(canvas, heartX, heartY, size);
        }
        accentPaint.setAlpha(255);

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawSleepyFace(Canvas canvas) {
        // Drowsy head movements
        wiggleAngle = (float) Math.sin(animProgress * PI * 0.8f) * 6f;
        float dropHead = (float) Math.sin(animProgress * PI * 0.6f) * 10f;

        canvas.save();
        canvas.translate(0, dropHead);
        canvas.rotate(wiggleAngle, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);
        paint.setStrokeWidth(14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Drowsy eyes
        Path leftEye = new Path();
        leftEye.moveTo(leftEyeX - eyeSize/2, leftEyeY);
        leftEye.quadTo(leftEyeX, leftEyeY + 10, leftEyeX + eyeSize/2, leftEyeY);
        canvas.drawPath(leftEye, paint);

        Path rightEye = new Path();
        rightEye.moveTo(rightEyeX - eyeSize/2, rightEyeY);
        rightEye.quadTo(rightEyeX, rightEyeY + 10, rightEyeX + eyeSize/2, rightEyeY);
        canvas.drawPath(rightEye, paint);

        paint.setStrokeWidth(12f);
        canvas.drawLine(getWidth() * 0.38f, getHeight() * 0.7f,
                getWidth() * 0.62f, getHeight() * 0.7f, paint);

        // Floating Z's with fade
        float zzzPhase = animProgress * PI * 2;
        float zzzBase = (float) Math.sin(zzzPhase) * 15f;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(accentColor);

        paint.setTextSize(48f);
        int alpha1 = (int)(160 + Math.sin(zzzPhase) * 70);
        paint.setAlpha(Math.max(0, Math.min(255, alpha1)));
        canvas.drawText("Z", getWidth() * 0.75f, getHeight() * 0.25f - zzzBase, paint);

        paint.setTextSize(38f);
        int alpha2 = (int)(140 + Math.sin(zzzPhase + 1) * 70);
        paint.setAlpha(Math.max(0, Math.min(255, alpha2)));
        canvas.drawText("z", getWidth() * 0.8f, getHeight() * 0.35f - zzzBase * 0.7f, paint);

        paint.setTextSize(28f);
        int alpha3 = (int)(120 + Math.sin(zzzPhase + 2) * 70);
        paint.setAlpha(Math.max(0, Math.min(255, alpha3)));
        canvas.drawText("z", getWidth() * 0.85f, getHeight() * 0.45f - zzzBase * 0.4f, paint);

        paint.setAlpha(255);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawExcitedFace(Canvas canvas) {
        // Energetic bouncing with squash/stretch
        float bouncePhase = animProgress * PI * 4;
        bounceOffset = (float) Math.abs(Math.sin(bouncePhase)) * 18f;
        squashStretch = 1f - (float) Math.abs(Math.sin(bouncePhase)) * 0.1f;
        sparklePhase = animProgress * PI * 6;

        canvas.save();
        canvas.translate(0, -bounceOffset);
        canvas.scale(1f / squashStretch, squashStretch, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);

        // Spinning stars
        float starRotation = animProgress * 360f;
        float starScale = 1f + (float) Math.sin(animProgress * PI * 4) * 0.2f;

        canvas.save();
        canvas.rotate(starRotation, leftEyeX, leftEyeY);
        canvas.scale(starScale, starScale, leftEyeX, leftEyeY);
        drawStar(canvas, leftEyeX, leftEyeY, eyeSize * 0.6f);
        canvas.restore();

        canvas.save();
        canvas.rotate(-starRotation, rightEyeX, rightEyeY);
        canvas.scale(starScale, starScale, rightEyeX, rightEyeY);
        drawStar(canvas, rightEyeX, rightEyeY, eyeSize * 0.6f);
        canvas.restore();

        paint.setStrokeWidth(16f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF mouthRect = new RectF(getWidth() * 0.28f, getHeight() * 0.5f,
                getWidth() * 0.72f, getHeight() * 0.82f);
        canvas.drawArc(mouthRect, 18, 144, false, paint);

        // Dynamic sparkles
        accentPaint.setColor(accentColor);
        for(int i = 0; i < 5; i++) {
            float angle = sparklePhase + i * PI * 0.4f;
            float radius = 60f + (float) Math.sin(angle) * 20f;
            float x = getWidth() * (0.35f + (float) Math.cos(angle) * 0.28f);
            float y = getHeight() * (0.4f + (float) Math.sin(angle) * 0.28f);
            float size = 10f + (float) Math.sin(angle * 2) * 5f;

            canvas.save();
            canvas.rotate((float)(angle * 180 / PI), x, y);
            drawSparkle(canvas, x, y, size);
            canvas.restore();
        }

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawConfusedFace(Canvas canvas) {
        // Head tilting with confusion
        wiggleAngle = (float) Math.sin(animProgress * PI * 2) * 15f;
        float eyeShift = (float) Math.sin(animProgress * PI * 2.5f) * 10f;

        canvas.save();
        canvas.rotate(wiggleAngle, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);

        // Asymmetric eyes
        canvas.drawCircle(leftEyeX, leftEyeY + eyeShift * 0.2f, eyeSize/2, paint);
        canvas.drawCircle(rightEyeX, rightEyeY - eyeShift * 0.2f, eyeSize/2.8f, paint);

        accentPaint.setColor(Color.WHITE);
        canvas.drawCircle(leftEyeX, leftEyeY + eyeShift * 0.2f, eyeSize * 0.25f, accentPaint);
        canvas.drawCircle(rightEyeX, rightEyeY - eyeShift * 0.2f, eyeSize * 0.15f, accentPaint);

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Wavy mouth
        Path wavyPath = new Path();
        wavyPath.moveTo(getWidth() * 0.35f, getHeight() * 0.7f);
        wavyPath.quadTo(getWidth() * 0.42f, getHeight() * 0.67f,
                getWidth() * 0.5f, getHeight() * 0.7f);
        wavyPath.quadTo(getWidth() * 0.58f, getHeight() * 0.73f,
                getWidth() * 0.65f, getHeight() * 0.7f);
        canvas.drawPath(wavyPath, paint);

        // Animated question mark
        paint.setTextSize(55f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(accentColor);
        canvas.save();
        float qRotation = (float) Math.sin(animProgress * PI * 2) * 18f;
        canvas.rotate(qRotation, getWidth() * 0.8f, getHeight() * 0.3f);
        canvas.drawText("?", getWidth() * 0.77f, getHeight() * 0.32f, paint);
        canvas.restore();

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawCoolFace(Canvas canvas) {
        // Smooth swaying
        float coolSway = (float) Math.sin(animProgress * PI) * 7f;
        float bobHead = (float) Math.sin(animProgress * PI * 1.5f) * 5f;

        canvas.save();
        canvas.translate(coolSway, bobHead);
        canvas.rotate((float) Math.sin(animProgress * PI * 0.8f) * 3f, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(darkColor);

        // Sunglasses with shine
        RectF leftLens = new RectF(leftEyeX - eyeSize/2 - 3, leftEyeY - eyeSize/3,
                leftEyeX + eyeSize/2 + 3, leftEyeY + eyeSize/3);
        canvas.drawRoundRect(leftLens, 15f, 15f, paint);

        RectF rightLens = new RectF(rightEyeX - eyeSize/2 - 3, rightEyeY - eyeSize/3,
                rightEyeX + eyeSize/2 + 3, rightEyeY + eyeSize/3);
        canvas.drawRoundRect(rightLens, 15f, 15f, paint);

        // Bridge
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(leftEyeX + eyeSize/2 + 3, leftEyeY,
                rightEyeX - eyeSize/2 - 3, rightEyeY, paint);

        // Shine effect
        accentPaint.setColor(Color.WHITE);
        accentPaint.setAlpha(120);
        canvas.drawCircle(leftEyeX - eyeSize/4, leftEyeY - eyeSize/6, 8f, accentPaint);
        canvas.drawCircle(rightEyeX - eyeSize/4, rightEyeY - eyeSize/6, 8f, accentPaint);
        accentPaint.setAlpha(255);

        // Cool smirk
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12f);
        Path smirk = new Path();
        smirk.moveTo(getWidth() * 0.35f, getHeight() * 0.7f);
        smirk.quadTo(getWidth() * 0.5f, getHeight() * 0.68f,
                getWidth() * 0.6f, getHeight() * 0.67f);
        canvas.drawPath(smirk, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawShyFace(Canvas canvas) {
        // Nervous swaying
        float shySway = (float) Math.sin(animProgress * PI * 1.5f) * 10f;
        float hideMove = (float) Math.abs(Math.sin(animProgress * PI * 0.8f)) * 6f;

        canvas.save();
        canvas.translate(shySway, hideMove);
        canvas.rotate((float) Math.sin(animProgress * PI) * 4f, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);

        // Shy eyes looking away
        canvas.drawOval(leftEyeX - eyeSize/2, leftEyeY - eyeSize/2,
                leftEyeX + eyeSize/2, leftEyeY + eyeSize/2, paint);
        canvas.drawOval(rightEyeX - eyeSize/2, rightEyeY - eyeSize/2,
                rightEyeX + eyeSize/2, rightEyeY + eyeSize/2, paint);

        accentPaint.setColor(Color.WHITE);
        float pupilOffset = eyeSize * 0.2f;
        float pupilWiggle = (float) Math.sin(animProgress * PI * 2) * 4f;
        canvas.drawCircle(leftEyeX + pupilOffset + pupilWiggle, leftEyeY + pupilOffset, eyeSize * 0.22f, accentPaint);
        canvas.drawCircle(rightEyeX + pupilOffset + pupilWiggle, rightEyeY + pupilOffset, eyeSize * 0.22f, accentPaint);

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF mouthRect = new RectF(getWidth() * 0.38f, getHeight() * 0.6f,
                getWidth() * 0.62f, getHeight() * 0.75f);
        canvas.drawArc(mouthRect, 20, 140, false, paint);

        // Big pulsing blush
        pulseScale = 1f + (float) Math.sin(animProgress * PI * 4) * 0.25f;
        accentPaint.setColor(accentColor);
        accentPaint.setAlpha(200);
        canvas.drawCircle(getWidth() * 0.18f, getHeight() * 0.55f, 48f * pulseScale, accentPaint);
        canvas.drawCircle(getWidth() * 0.82f, getHeight() * 0.55f, 48f * pulseScale, accentPaint);

        canvas.drawCircle(getWidth() * 0.25f, getHeight() * 0.6f, 24f * pulseScale * 0.8f, accentPaint);
        canvas.drawCircle(getWidth() * 0.75f, getHeight() * 0.6f, 24f * pulseScale * 0.8f, accentPaint);
        accentPaint.setAlpha(255);

        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawThinkingFace(Canvas canvas) {
        // Thoughtful tilting
        float tiltAngle = (float) Math.sin(animProgress * PI * 1.2f) * 8f;
        float headBob = (float) Math.sin(animProgress * PI * 0.8f) * 4f;

        canvas.save();
        canvas.translate(0, headBob);
        canvas.rotate(tiltAngle, getWidth() / 2f, getHeight() / 2f);

        paint.setColor(primaryColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawOval(leftEyeX - eyeSize/2, leftEyeY - eyeSize/2.5f,
                leftEyeX + eyeSize/2, leftEyeY + eyeSize/2.5f, paint);
        canvas.drawOval(rightEyeX - eyeSize/2, rightEyeY - eyeSize/2.5f,
                rightEyeX + eyeSize/2, rightEyeY + eyeSize/2.5f, paint);

        accentPaint.setColor(Color.WHITE);
        float pupilShift = (float) Math.sin(animProgress * PI * 1.5f) * 6f;
        canvas.drawCircle(leftEyeX + pupilShift, leftEyeY - eyeSize/6, eyeSize * 0.2f, accentPaint);
        canvas.drawCircle(rightEyeX + pupilShift, rightEyeY - eyeSize/6, eyeSize * 0.2f, accentPaint);

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(getWidth() * 0.38f, getHeight() * 0.7f,
                getWidth() * 0.62f, getHeight() * 0.7f, paint);

        // Floating thought cloud
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(accentColor);
        paint.setAlpha(180);

        float cloudFloat = (float) Math.sin(animProgress * PI * 2) * 10f;
        canvas.save();
        canvas.translate(0, cloudFloat);
        drawThoughtCloud(canvas, getWidth() * 0.75f, getHeight() * 0.25f, 42f);
        canvas.restore();

        paint.setAlpha(255);
        canvas.restore();
    }

    private void drawLaughingFace(Canvas canvas) {
        // Intense laughing shake
        bounceOffset = (float) Math.abs(Math.sin(animProgress * PI * 5)) * 15f;
        float shakeLaugh = (float) Math.sin(animProgress * PI * 8) * 4f;

        canvas.save();
        canvas.translate(shakeLaugh, -bounceOffset);

        paint.setColor(primaryColor);
        paint.setStrokeWidth(18f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Eyes closed with joy
        Path leftEyeClosed = new Path();
        leftEyeClosed.moveTo(leftEyeX - eyeSize/2, leftEyeY);
        leftEyeClosed.quadTo(leftEyeX, leftEyeY + 14, leftEyeX + eyeSize/2, leftEyeY);
        canvas.drawPath(leftEyeClosed, paint);

        Path rightEyeClosed = new Path();
        rightEyeClosed.moveTo(rightEyeX - eyeSize/2, rightEyeY);
        rightEyeClosed.quadTo(rightEyeX, rightEyeY + 14, rightEyeX + eyeSize/2, rightEyeY);
        canvas.drawPath(rightEyeClosed, paint);

        // Big open mouth
        paint.setStrokeWidth(16f);
        RectF bigMouth = new RectF(getWidth() * 0.25f, getHeight() * 0.5f,
                getWidth() * 0.75f, getHeight() * 0.85f);
        canvas.drawArc(bigMouth, 15, 150, false, paint);

        // Joy tears
        paint.setStyle(Paint.Style.FILL);
        accentPaint.setColor(accentColor);
        accentPaint.setAlpha(160);

        for(int i = 0; i < 3; i++) {
            float tearX = leftEyeX - eyeSize/3 + i * 10;
            float tearPhase = animProgress * PI * 3 + i;
            float tearY = leftEyeY + eyeSize/2 + (float) Math.sin(tearPhase) * 8f;
            canvas.drawCircle(tearX, tearY, 7f, accentPaint);
        }

        for(int i = 0; i < 3; i++) {
            float tearX = rightEyeX + eyeSize/3 - i * 10;
            float tearPhase = animProgress * PI * 3 + i + 1;
            float tearY = rightEyeY + eyeSize/2 + (float) Math.sin(tearPhase) * 8f;
            canvas.drawCircle(tearX, tearY, 7f, accentPaint);
        }

        accentPaint.setAlpha(255);
        canvas.restore();
    }

    // ========== HELPER DRAWING METHODS ==========

    private void drawHeart(Canvas canvas, float x, float y, float size) {
        Path heart = new Path();
        heart.moveTo(x, y + size * 0.3f);
        heart.cubicTo(x - size * 0.6f, y - size * 0.3f,
                x - size * 0.8f, y + size * 0.2f,
                x, y + size * 0.8f);
        heart.cubicTo(x + size * 0.8f, y + size * 0.2f,
                x + size * 0.6f, y - size * 0.3f,
                x, y + size * 0.3f);
        canvas.drawPath(heart, paint);
    }

    private void drawSmallHeart(Canvas canvas, float x, float y, float size) {
        Path heart = new Path();
        heart.moveTo(x, y + size * 0.3f);
        heart.cubicTo(x - size * 0.6f, y - size * 0.3f,
                x - size * 0.8f, y + size * 0.2f,
                x, y + size * 0.8f);
        heart.cubicTo(x + size * 0.8f, y + size * 0.2f,
                x + size * 0.6f, y - size * 0.3f,
                x, y + size * 0.3f);
        canvas.drawPath(heart, accentPaint);
    }

    private void drawStar(Canvas canvas, float x, float y, float size) {
        Path star = new Path();
        float angle = PI / 5;

        for (int i = 0; i < 10; i++) {
            float radius = (i % 2 == 0) ? size : size * 0.4f;
            float pointX = x + (float) Math.cos(i * angle - PI / 2) * radius;
            float pointY = y + (float) Math.sin(i * angle - PI / 2) * radius;

            if (i == 0) {
                star.moveTo(pointX, pointY);
            } else {
                star.lineTo(pointX, pointY);
            }
        }
        star.close();
        canvas.drawPath(star, paint);
    }

    private void drawSparkle(Canvas canvas, float x, float y, float size) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(accentColor);

        canvas.drawLine(x - size, y, x + size, y, paint);
        canvas.drawLine(x, y - size, x, y + size, paint);
        canvas.drawLine(x - size * 0.7f, y - size * 0.7f, x + size * 0.7f, y + size * 0.7f, paint);
        canvas.drawLine(x - size * 0.7f, y + size * 0.7f, x + size * 0.7f, y - size * 0.7f, paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawThoughtCloud(Canvas canvas, float x, float y, float size) {
        accentPaint.setColor(accentColor);

        canvas.drawCircle(x, y, size * 0.6f, accentPaint);
        canvas.drawCircle(x + size * 0.4f, y - size * 0.2f, size * 0.5f, accentPaint);
        canvas.drawCircle(x - size * 0.4f, y - size * 0.2f, size * 0.45f, accentPaint);

        canvas.drawCircle(x - size * 0.8f, y + size * 0.6f, size * 0.2f, accentPaint);
        canvas.drawCircle(x - size * 1.1f, y + size * 0.9f, size * 0.12f, accentPaint);
    }

    // ========== ANIMATION CONTROL ==========

    private void startBlinkAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Math.random() < 0.3) {
                    performBlink();
                }
                handler.postDelayed(this, 2500 + (long)(Math.random() * 2000));
            }
        }, 2000);
    }

    private void performBlink() {
        isBlinking = true;
        final Handler handler = new Handler();
        final long startTime = System.currentTimeMillis();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = elapsed / 120f; // Faster blink

                if (progress < 1f) {
                    blinkProgress = easeInOutQuad(progress);
                    invalidate();
                    handler.postDelayed(this, 16);
                } else if (progress < 2f) {
                    blinkProgress = easeInOutQuad(2f - progress);
                    invalidate();
                    handler.postDelayed(this, 16);
                } else {
                    blinkProgress = 0f;
                    isBlinking = false;
                    invalidate();
                }
            }
        });
    }

    private void startContinuousAnimation() {
        final Handler handler = new Handler();
        animStartTime = System.currentTimeMillis();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - animStartTime;
                animProgress = (elapsed % 3000) / 3000f;

                // Handle smooth transitions
                if (isTransitioning && transitionProgress < 1f) {
                    transitionProgress += 0.02f; // Smooth 50-frame transition
                    if (transitionProgress >= 1f) {
                        transitionProgress = 1f;
                        isTransitioning = false;
                        currentExpression = targetExpression;
                    }
                }

                invalidate();
                handler.postDelayed(this, 16); // 60 FPS
            }
        });
    }

    // ========== PUBLIC METHODS ==========

    public void setExpression(String expression) {
        if (!expression.equals(currentExpression)) {
            targetExpression = expression;
            transitionProgress = 0f;
            isTransitioning = true;
            idleStartTime = System.currentTimeMillis(); // Reset idle timer
        }
        invalidate();
    }

    public String getExpression() {
        return currentExpression;
    }

    public void setExpressionImmediate(String expression) {
        currentExpression = expression;
        targetExpression = expression;
        transitionProgress = 1f;
        isTransitioning = false;
        invalidate();
    }
}