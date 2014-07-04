package com.nexusbr.gv.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Teste extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	Canvas canvas;
	boolean drawPoint = false;
	boolean drawLine = false;
	ArrayList<Point> points;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Teste frame = new Teste();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Teste() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 463);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		points = new ArrayList<Point>();
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JButton btnPoint = new JButton("Create Point");
		btnPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				activeDrawPoint();
			}
		});
		
		JButton btnClearCanvas = new JButton("Clear Canvas");
		btnClearCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearCanvas();
			}
		});
		toolBar.add(btnClearCanvas);
		toolBar.add(btnPoint);
		
		JButton btnCreateLine = new JButton("Create Line");
		btnCreateLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activeDrawLine();
			}			
		});
		toolBar.add(btnCreateLine);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		canvas = new Canvas();
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(drawPoint){
					createPoint(e.getX(), e.getY());
				}
				if(drawLine){
					
				}
			}
		});
		canvas.setBounds(10, 10, 574, 362);
		canvas.setBackground(Color.WHITE);
		
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		panel.add(canvas);				
	} 

	public void clearCanvas() {
		points.clear();
		paintComponent(canvas.getGraphics());
	}

	public void createPoint(int x, int y) {
		if(drawPoint){
			Coordinate coord = new Coordinate(x,y);
			Point point = new GeometryFactory().createPoint(coord);
			points.add(point);
		}
		paintComponent(canvas.getGraphics());
	}

	public void activeDrawPoint() {
		drawLine = false;
		drawPoint = true;
	}	
	
	public void activeDrawLine() {
		drawLine = true;
		drawPoint = false;
	}
	
	public void update(Graphics g) {
		canvas.update(g);
	}
	
	public void paintComponent(Graphics g) {
		g.clearRect(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
		for (Point point : points) {
			int x = (int) point.getCoordinate().x;
			int y = (int) point.getCoordinate().y;	
			g.setColor(Color.YELLOW);
			g.fillOval(x-5, y-5, 10, 10);
			g.setColor(Color.BLACK);
			g.drawOval(x-5, y-5, 10, 10);
		}		
	}
}
