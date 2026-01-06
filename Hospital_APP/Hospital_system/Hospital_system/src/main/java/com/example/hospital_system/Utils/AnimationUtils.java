package com.example.hospital_system.Utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationUtils {

    /**
     * Fade in animation for a node
     */
    public static void fadeIn(Node node, double duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Fade in with default duration
     */
    public static void fadeIn(Node node) {
        fadeIn(node, 400);
    }

    /**
     * Slide in from right animation
     */
    public static void slideInFromRight(Node node, double duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromX(100);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        parallel.play();
    }

    /**
     * Slide in from left animation
     */
    public static void slideInFromLeft(Node node, double duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromX(-100);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        parallel.play();
    }

    /**
     * Scale up animation (pop effect)
     */
    public static void scaleUp(Node node, double duration) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(duration), node);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.play();
    }

    /**
     * Bounce animation
     */
    public static void bounce(Node node) {
        ScaleTransition scale1 = new ScaleTransition(Duration.millis(100), node);
        scale1.setToX(1.1);
        scale1.setToY(1.1);

        ScaleTransition scale2 = new ScaleTransition(Duration.millis(100), node);
        scale2.setToX(1.0);
        scale2.setToY(1.0);

        SequentialTransition sequence = new SequentialTransition(scale1, scale2);
        sequence.play();
    }

    /**
     * Shake animation for errors
     */
    public static void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    /**
     * Pulse animation (gentle scale up and down)
     */
    public static void pulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), node);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    /**
     * Add hover effect with shadow and scale
     */
    public static void addHoverEffect(Node node) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(10);
        shadow.setOffsetY(3);

        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();
            node.setEffect(shadow);
        });

        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            node.setEffect(null);
        });
    }

    /**
     * Add button hover effect
     */
    public static void addButtonHover(Node button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    /**
     * Fade transition between scenes
     */
    public static void fadeTransition(Node oldNode, Node newNode, double duration) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration / 2), oldNode);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(duration / 2), newNode);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        SequentialTransition sequence = new SequentialTransition(fadeOut, fadeIn);
        sequence.play();
    }

    /**
     * Staggered fade in for multiple nodes
     */
    public static void staggeredFadeIn(Node[] nodes, double delayBetween) {
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            final double delay = i * delayBetween;

            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> fadeIn(node, 400));
            pause.play();
        }
    }

    /**
     * Rotate animation
     */
    public static void rotate(Node node, double degrees, double duration) {
        RotateTransition rotate = new RotateTransition(Duration.millis(duration), node);
        rotate.setByAngle(degrees);
        rotate.play();
    }

    /**
     * Success pulse (green glow effect)
     */
    public static void successPulse(Node node) {
        DropShadow greenGlow = new DropShadow();
        greenGlow.setColor(Color.rgb(76, 175, 80, 0.6));
        greenGlow.setRadius(20);

        node.setEffect(greenGlow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> node.setEffect(null)));
        timeline.play();
    }

    /**
     * Error pulse (red glow effect)
     */
    public static void errorPulse(Node node) {
        DropShadow redGlow = new DropShadow();
        redGlow.setColor(Color.rgb(244, 67, 54, 0.6));
        redGlow.setRadius(20);

        node.setEffect(redGlow);
        shake(node);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> node.setEffect(null)));
        timeline.play();
    }
}
