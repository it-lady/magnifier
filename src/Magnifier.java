import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Magnifier {
    private static final int FPS = 60; // animator's target frames per second

    public static void main(String[] args) {
            Frame testFrame = new Frame("PGRF - project - magnifier on a raster picture");
            testFrame.setSize(600, 400);

            // setup OpenGL Version 2
            GLProfile profile = GLProfile.get(GLProfile.GL2);
            GLCapabilities capabilities = new GLCapabilities(profile);
            capabilities.setRedBits(8);
            capabilities.setBlueBits(8);
            capabilities.setGreenBits(8);
            capabilities.setAlphaBits(8);
            capabilities.setDepthBits(24);

            GLCanvas canvas = new GLCanvas(capabilities);
            RenderProcessor renderer = new RenderProcessor();
            canvas.addGLEventListener(renderer);
            canvas.addMouseListener(renderer);
            canvas.addMouseMotionListener(renderer);
            canvas.setSize( 1024, 768 );
            testFrame.addKeyListener(renderer);

            testFrame.add(canvas);

            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

            testFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread() {
                        @Override
                        public void run() {
                            if (animator.isStarted()) animator.stop();
                            System.exit(0);
                        }
                    }.start();
                }
            });
            testFrame.pack();
            testFrame.setVisible(true);
            animator.start();



    }

}