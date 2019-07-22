import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import utils.OglUtils;

public class RenderProcessor implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    GL2 gl;
    GLU glu;
    GLUT glut;

    // File chooser
    final JFileChooser fc = new JFileChooser();
    // Texture is loading from an external object
    File file = new File("./res/Witcher.jpg");
    Texture texture;

    private int width, height;
    private int ox, oy;

    @Override
    public void init(GLAutoDrawable glDrawable) {
        gl = glDrawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();

        OglUtils.printOGLparameters(gl);

        try {
            System.out.println("Texture is loading...");
            texture = TextureIO.newTexture(file, true);
        } catch (IOException e) {
            System.err.println("There is some problem with texture loading");
        }
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2 gl = glDrawable.getGL().getGL2();

        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluLookAt(20, 0, 0, 0, 0, 0, 0, 0, 1);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        // Texture repeating
        int paramTex = GL2.GL_CLAMP_TO_BORDER;

        // Texture and color combination
        int paramTexApp = GL2.GL_REPLACE;

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, paramTex);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, paramTex);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, paramTexApp);

        // Magnifier move
        gl.glLoadIdentity();

        float goalRadius = 0.3f;
        float actualRadius = 0;
        float initialStep = 0.1f;
        int radiusStep = 1;

        while (actualRadius < goalRadius) {
            actualRadius += initialStep;
            initialStep = initialStep / 1.4f;
            drawFilledQuads(gl, (ox / (width - width / 2.0f)) - 1, -1 * ((oy / (height - height / 2.0f)) - 1),
                    actualRadius - initialStep * 1.4f, actualRadius, radiusStep);
            radiusStep++;
        }

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();

        // Color
        gl.glBegin(GL2.GL_QUADS);

        // Tops and texture
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(-1, -1);
        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(-1, 1);
        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(1, 1);
        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(1, -1);
        gl.glEnd();

        String help ="[L]oad picture";

        OglUtils.drawStr2D(glDrawable, 5, height - 20, help);
        OglUtils.drawStr2D(glDrawable, width - 250, 3, " (c) PGRF UHK, Al\u017Eb\u011Bta Pulpánová");

    }

    public void drawFilledQuads(GL2 gl, float x, float y, float radius, float radius2, int steptest) {
        int triangleCount = 20;

        double constantTwoPi = 2.0f * Math.PI;
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Set color for magnifier testing
        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL2.GL_QUADS);

        for (int i = 0; i < triangleCount; i++) {
            double x1t, y1t, x2t, y2t, x3t, y3t, x4t, y4t, ax3t, ay3t, ax4t, ay4t, ax1t, ay1t, ax2t, ay2t;

            x1t = (radius * Math.cos(i * constantTwoPi / triangleCount));
            y1t = ((radius * Math.sin(i * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            //Second coordinates calculation - for zoom and proper texture mapping at all points
            ax1t = ((0.1f * (steptest - 1)) * Math.cos(i * constantTwoPi / triangleCount));
            ay1t = (((0.1f * (steptest - 1)) * Math.sin(i * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            gl.glTexCoord2d((x / 2) + (ax1t / 4) + 0.5, (y / 2) + (ay1t / 4) + 0.5);
            gl.glVertex2d(x + x1t, y + y1t);

            x2t = (radius * Math.cos((i + 1) * constantTwoPi / triangleCount));
            y2t = ((radius * Math.sin((i + 1) * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            ax2t = ((0.1f * (steptest - 1)) * Math.cos((i + 1) * constantTwoPi / triangleCount));
            ay2t = (((0.1f * (steptest - 1)) * Math.sin((i + 1) * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            gl.glTexCoord2d((x / 2) + (ax2t / 4) + 0.5, (y / 2) + (ay2t / 4) + 0.5);
            gl.glVertex2d(x + x2t, y + y2t);

            x3t = (radius2 * Math.cos(i * constantTwoPi / triangleCount));
            y3t = ((radius2 * Math.sin(i * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            x4t = (radius2 * Math.cos((i + 1) * constantTwoPi / triangleCount));
            y4t = ((radius2 * Math.sin((i + 1) * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            ax3t = ((0.1f * steptest) * Math.cos(i * constantTwoPi / triangleCount));
            ay3t = (((0.1f * steptest) * Math.sin(i * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            ax4t = ((0.1f * steptest) * Math.cos((i + 1) * constantTwoPi / triangleCount));
            ay4t = (((0.1f * steptest) * Math.sin((i + 1) * constantTwoPi / triangleCount)) * 4.0f) / 3.0f;

            gl.glTexCoord2d((x / 2) + (ax4t / 4) + 0.5, (y / 2) + (ay4t / 4) + 0.5);
            gl.glVertex2d(x + x4t, y + y4t);

            gl.glTexCoord2d((x / 2) + (ax3t / 4) + 0.5, (y / 2) + (ay3t / 4) + 0.5);
            gl.glVertex2d(x + x3t, y + y3t);

        }
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        double s = 4 / 3.0;
        if ((width / s) > height) {
            width = (int) (height * s);
        } else {
            height = (int) (width / s);
        }
        gl.glViewport(0, 0, width, height);
        glDrawable.getGL().getGL2().glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
        }
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ox = e.getX();
        oy = e.getY();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ox = e.getX();
        oy = e.getY();

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    // FileChooser open
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_L) {
            int returnVal = fc.showOpenDialog(fc);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                System.out.println("File is opening: " + file.getName());

                try {
                    gl.getContext().makeCurrent();
                    texture = TextureIO.newTexture(file, true);
                } catch (Exception ex) {
                    System.out.println("The texture did not open.");

                }

            } else {
                System.out.println("Texture opening was cancelled by user.");
            }

        }
    }
}