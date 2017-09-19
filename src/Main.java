import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

public class Main extends JFrame implements Runnable, KeyListener {
	double x, y, vx, vy, speed;
	double barx, bary;
	boolean running;
	boolean done;
	Rectangle[] blocks = new Rectangle[16];
	boolean[] blockstatus = new boolean[16];
	//graphics stuffs
	AffineTransform identity = new AffineTransform();
	BufferedImage backbuffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = backbuffer.createGraphics();
	//random number generator
	Random rand = new Random();
	//thread to update game
	Thread gameloop = new Thread(this);
	//constructor
	Main(){
		super("DX Ball");
		this.setSize(800,  600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		x = this.getWidth()/2;
		y = this.getHeight() - 50;
		barx = this.getWidth()/2 - 100;
		bary = this.getHeight() - 30;
		speed = 4.0;
		vx = speed;
		vy = -speed;
		//create the blocks
		for(int i = 0; i<16; i++) {
			blocks[i] = new Rectangle(i*50, 20 + rand.nextInt(200), 50, 20);
			blockstatus[i] = true;
		}
		this.addKeyListener(this);
		running = true;
		done = false;
		this.setVisible(true);
	}
	public void paint(Graphics g) {
		g2d.setTransform(identity);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if(!running) {
			g2d.setColor(Color.RED);
			g2d.setFont(new Font("Tahoma", Font.ITALIC, 40));
			g2d.setTransform(identity);
			g2d.translate(this.getWidth()/2 - 200, this.getHeight()/2);
			g2d.drawString("Game Over Asshole!", 0, 0);
			g.drawImage(backbuffer, 0, 0, this);
			return;
		}
		if(done) {
			g2d.setColor(Color.RED);
			g2d.setFont(new Font("Tahoma", Font.ITALIC, 40));
			g2d.setTransform(identity);
			g2d.translate(this.getWidth()/2 - 350, this.getHeight()/2);
			g2d.drawString("Congrats!", 0, 0);
			g.drawImage(backbuffer, 0, 0, this);
			return;
		}
		//draw the ball
		g2d.setColor(Color.BLACK);
		g2d.setTransform(identity);
		g2d.translate(x, y);
		g2d.fillOval(0, 0, 20, 20);
		//draw the bar
		g2d.setColor(new Color(rand.nextInt()));
		g2d.setTransform(identity);
		g2d.translate(barx, bary);
		g2d.fillRect(0, 0, 200, 30);
		//draw the blocks
		for(int i = 0; i<16; i++) {
			if(blockstatus[i]) {
				g2d.setColor(new Color(rand.nextInt()));
				g2d.setTransform(identity);
				g2d.fill(blocks[i]);
			}
		}
		g.drawImage(backbuffer, 0, 0, this);
	}
	public static void main(String[] args) {
		new Main();
	}
	public void run() {
		//System.out.println("Run method");
		Thread t = Thread.currentThread();
		while(t==gameloop) {
			if(!running) {
				break;
			}
			if(done) {
				break;
			}
			try {
				updateGame();
				Thread.sleep(20);
			}
			catch(InterruptedException ex) {
				
			}
			repaint();
		}
	}
	public void updateGame() {
		x+=vx;
		y+=vy;
		if(x>this.getWidth()-20) {
			vx = -speed;
		}
		if(y>this.getHeight()-20) {
			System.out.println("Game over!");
			running = false;
			done = true;
		}
		if(x<10) {
			vx = speed;
		}
		if(y<10) {
			vy = speed;
		}
		checkCollision();
	}
	public void checkCollision() {
		//collision with bar
		if(y==bary-20 && x>=barx && x<=barx+200) {
			vy*=-1;
		}
		//collision with blocks
		for(int i = 0; i<16; i++) {
			//check collision with every alive box
			if(blockstatus[i]) {
				if(blocks[i].intersects(new Rectangle((int)x, (int)y, 20, 20))) {
					blockstatus[i] = false;
					vy*=-1;
				}
			}
		}
		//check how many blocks remain
		for(int i = 0; i<16; i++) {
			if(blockstatus[i]) {
				return;
			}
		}
		done = true;
	}
	public void keyTyped(KeyEvent k) {
		
	}
	public void keyReleased(KeyEvent k) {
		
	}
	public void keyPressed(KeyEvent k) {
		//System.out.println("key pressed");
		int keyCode = k.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_LEFT:
			if(barx>5) {
				barx-=5;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(barx<this.getWidth() - 200) {
				barx+=5;
			}
			break;
		case KeyEvent.VK_ENTER:
			if(!done) {
				gameloop.start();
			}
			break;
		}
	}
}
