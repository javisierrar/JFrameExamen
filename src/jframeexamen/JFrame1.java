
package jframeexamen;



import Base;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.JFrame;


public class JFrame1 extends JFrame implements KeyListener, Runnable {

    private final int iMAXANCHO = 10; // maximo numero de personajes por ancho
    private final int iMAXALTO = 8;  // maxuimo numero de personajes por alto
    private Base basPrincipal;         // Objeto principal
    private LinkedList<Base> lklDiddy; // lista de diddys
    private LinkedList<Base> lklChimpy; //lista de chimpys
    private int iDireccion; // Direccion de juanito
    private int iVidas; //vidas de juanito
    private int iVelocidad; //velocidad de los chimpys y diddys
    private int iPuntos; //puntaje de juanito
    private boolean bPausa; //booleano para pausar el juego
    private boolean bFin; //booleano para acabar el juego

    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private SoundClip adcSonidoChimpy;   // Objeto sonido de Chimpy
    private SoundClip adcSonidoDiddy;
    private Image imaGameOver;   // Imagen a proyectar en Applet	

    /**
     * init
     *
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos a
     * usarse en el <code>Applet</code> y se definen funcionalidades.
     *
     */
    public JFrame1() {
        this.setSize(800, 400);

        URL urlImagenPrincipal = this.getClass().getResource("juanito.gif");

        // se crea el objeto para principal 
        basPrincipal = new Base(0, 0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la esquina superior izquierda del Applet 
        basPrincipal.setX(getWidth() / 2);
        basPrincipal.setY(getHeight() / 2);

        // defino la imagen del malo
        URL urlImagenMalo = this.getClass().getResource("chimpy.gif");

        // se crea el objeto para malo 
        int iPosX = (iMAXANCHO - 1) * getWidth() / iMAXANCHO;
        int iPosY = (iMAXALTO - 1) * getHeight() / iMAXALTO;
        //genero la lista
        lklChimpy = new LinkedList();
        //genero un numero azar de 3 a 8
        int iAzar = (int) (Math.random() * 3) + 5;
        iVidas = (int) (Math.random() * 3) + 4;
        iPuntos = 0;
        iDireccion = 0;
        iVelocidad = 3;
        bPausa = true;
        bFin = true;
        //genero cada chimpy y lo anado a la lista
        for (int iI = 0; iI < iAzar; iI++) {
            iPosX = (int) (Math.random() * (3 * getWidth() * 3));
            iPosY = (int) (Math.random() * (3 * getHeight() / 4));
            Base basMalo = new Base(iPosX, iPosY, getWidth() / iMAXANCHO,
                    getHeight() / iMAXALTO,
                    Toolkit.getDefaultToolkit().getImage(urlImagenMalo));
            lklChimpy.add(basMalo);
        }
        // defino la imagen del diddy
        URL urlImagenDiddy = this.getClass().getResource("diddy.gif");

        // se crea el objeto para malo 
        iPosX = (iMAXANCHO - 1) * getWidth() / iMAXANCHO;
        iPosY = (iMAXALTO - 1) * getHeight() / iMAXALTO;
        //genero la lista
        lklDiddy = new LinkedList();
        //genero un numero azar de 3 a 8
        iAzar = (int) (Math.random() * 3) + 5;

        //genero cada diddy y lo anado a la lista
        for (int iI = 0; iI < iAzar; iI++) {
            iPosX = (int) (Math.random() * (3 * getWidth() * - 2));
            iPosY = (int) (Math.random() * (3 * getHeight() / 4));
            Base basDiddy = new Base(iPosX, iPosY, getWidth() / iMAXANCHO, getHeight() / iMAXALTO,
                    Toolkit.getDefaultToolkit().getImage(urlImagenDiddy));
            lklDiddy.add(basDiddy);
        }

        //URL urlSonidoChimpy = this.getClass().getResource("monkey1.wav");
        adcSonidoChimpy = new SoundClip("monkey1.wav");
        adcSonidoChimpy.play();

        // URL urlSonidoDiddy = this.getClass().getResource(new SoundClip("monkey1.wav"); );
        adcSonidoDiddy = new SoundClip("monkey1.wav");

        URL urlPerder = this.getClass().getResource("gameover.png");
        imaGameOver = Toolkit.getDefaultToolkit().getImage(urlPerder);
        addKeyListener(this);
    }

    public void start() {
        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    /**
     * run
     *
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones de
     * nuestro juego.
     *
     */
    public void run() {
        /* mientras dure el juego, se actualizan posiciones de jugadores
         se checa si hubo colisiones para desaparecer jugadores o corregir
         movimientos y se vuelve a pintar todo
         */
        while (iVidas > 0 && bFin) {
            if (bPausa) {
                actualiza();
                checaColision();
            }
            repaint();
            try {
                // El thread se duerme.
                Thread.sleep(20);
            } catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego "
                        + iexError.toString());
            }
        }
    }

    /**
     * actualiza
     *
     * Metodo que actualiza la posicion de los objetos
     *
     */
    public void actualiza() {
        //Los movimientos de juanito
        switch (iDireccion) {
            case 1: { //se mueve hacia arriba
                basPrincipal.setY(basPrincipal.getY() - (getHeight() / iMAXALTO));
                iDireccion = 0;
                break;
            }
            case 2: { //se mueve hacia abajo
                basPrincipal.setY(basPrincipal.getY() + (getHeight() / iMAXALTO));
                iDireccion = 0;
                break;
            }
            case 3: { //se mueve hacia izquierda
                basPrincipal.setX(basPrincipal.getX() - (getWidth() / iMAXANCHO));
                iDireccion = 0;
                break;
            }
            case 4: { //se mueve hacia derecha
                basPrincipal.setX(basPrincipal.getX() + (getWidth() / iMAXANCHO));
                iDireccion = 0;
                break;
            }
        }
        for (Base basDiddy : lklDiddy) {
            //actualizo a los diddys moviendose
            basDiddy.setX(basDiddy.getX() + iVelocidad);
        }
        for (Base basChimpy : lklChimpy) {
            //actualizo a los chimpys moviendose
            basChimpy.setX(basChimpy.getX() - iVelocidad);
        }
    }

    /**
     * checaColision
     *
     * Metodo usado para checar la colision entre objetos
     *
     */
    public void checaColision() {
        if (basPrincipal.getY() < 0) { // y esta pasando el limite
            iDireccion = 2;     // se cambia la direccion para abajo
        }
        if (basPrincipal.getY() + basPrincipal.getAlto() > getHeight()) {
            iDireccion = 1;     // se cambia la direccion para arriba
        }
        if (basPrincipal.getX() < 0) { // y se sale del applet
            iDireccion = 4;       // se cambia la direccion a la derecha
        }
        if (basPrincipal.getX() + basPrincipal.getAncho() > getWidth()) {
            iDireccion = 3;       // se cambia direccion a la izquierda
        }
        for (Base basChimpy : lklChimpy) {
            if (basChimpy.intersecta(basPrincipal)) {
                basChimpy.setX((int) (Math.random() * (3 * getWidth() * 3)));
                basChimpy.setY((int) (Math.random() * (3 * getHeight() / 4)));
                iPuntos += 10;
                adcSonidoChimpy.play();
            }
            if (basChimpy.getX() < 0) {
                basChimpy.setX((int) (Math.random() * (3 * getWidth() * 3)));
                basChimpy.setY((int) (Math.random() * (3 * getHeight() / 4)));
            }
        }
        for (Base basDiddy : lklDiddy) {
            if (basDiddy.intersecta(basPrincipal)) {
                basDiddy.setX((int) (Math.random() * (3 * getWidth() * -1)));
                basDiddy.setY((int) (Math.random() * (3 * getHeight() / 4)));
                iVidas--;
                iVelocidad++;
                adcSonidoDiddy.play();
            }
            if (basDiddy.getX() + basDiddy.getAncho() > getWidth()) {
                basDiddy.setX((int) (Math.random() * (3 * getWidth() * -1)));
                basDiddy.setY((int) (Math.random() * (3 * getHeight() / 4)));
            }
        }
    }

    /**
     * update
     *
     * Metodo sobrescrito de la clase <code>Applet</code>, heredado de la clase
     * Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y define cuando
     * usar ahora el paint
     *
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     *
     */
    public void paint(Graphics graGrafico) {
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null) {
            imaImagenApplet = createImage(this.getSize().width,
                    this.getSize().height);
            graGraficaApplet = imaImagenApplet.getGraphics();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("Ciudad.png");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
        graGraficaApplet.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor(getForeground());
        paint1(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage(imaImagenApplet, 0, 0, this);
    }

    public void paint1(Graphics graDibujo) {
        if (iVidas < 0 || bFin) {
            // si la imagen ya se cargo
            if (basPrincipal != null) {
                // graDibujo.drawImage(basPrincipal.getImagen(), basPrincipal.getX(),basPrincipal.getY(), this);
                //Dibuja la imagen de principal en el Applet
                basPrincipal.paint(graDibujo, this);
                //Dibuja la imagen de malo en el Applet
                for (Base basDiddy : lklChimpy) {
                    //Dibuja la iagen de dumbo en el Applet
                    basDiddy.paint(graDibujo, this);
                }
                for (Base basDiddy : lklDiddy) {
                    //Dibuja la iagen de dumbo en el Applet
                    basDiddy.paint(graDibujo, this);
                }
                graDibujo.setColor(Color.black);
                graDibujo.drawString("Puntos: " + iPuntos, 20, 20);
                graDibujo.drawString("Vidas: " + iVidas, 100, 20);
            } // sino se ha cargado se dibuja un mensaje 
            else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
            }
        } else if (bFin == false) {
            graDibujo.drawImage(imaGameOver, 0, 0, this);
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        // si presiono flecha para arriba
        if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            iDireccion = 1;  // cambio la dirección arriba
        } // si presiono flecha para abajo
        else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            iDireccion = 2;   // cambio la direccion para abajo
        } // si presiono flecha a la izquierda
        else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            iDireccion = 3;   // cambio la direccion a la izquierda
        } // si presiono flecha a la derecha
        else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            iDireccion = 4;   // cambio la direccion a la derecha
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_P) {
            bPausa = !bPausa;

        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            bFin = false;  // se acaba el juego
        }
    }

    
}
