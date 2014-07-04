package com.nexusbr.gv.view.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.nexusbr.gv.services.IntersectGeometry;
import com.nexusbr.gv.services.LineCreatorService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * view to create a parallel line
 * @author Severino, Bruno de Oliveira
 * @version 1.0
 */
public class ParallelLineWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel buttonPane;
	private PanelAngle panel;
	public Geometry geomLineAngle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new ParallelLineWindow();		
	}	

	/**
	 * Create the dialog.
	 */
	public ParallelLineWindow() {
		setTitle("Parallel Line");
		setResizable(false);
		setBounds(100, 100, 267, 115);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setBackground(new Color(229, 235, 242));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblLength = new JLabel("Length:");
		lblLength.setBounds(10, 11, 46, 25);
		contentPanel.add(lblLength);

		textField = new JTextField();
		textField.setBounds(63, 11, 98, 25);
		contentPanel.add(textField);
		textField.setColumns(10);

		JLabel lblAngle = new JLabel("Angle:");
		lblAngle.setBounds(10, 52, 46, 25);
		contentPanel.add(lblAngle);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(108, 52, 53, 25);
		contentPanel.add(textField_1);

		geomLineAngle = new LineCreatorService().createLine(new Coordinate[]{new Coordinate(20, 20), new Coordinate(40, 20)});

		panel = new PanelAngle(this);
		panel.setBounds(63, 40, 41, 41);
		contentPanel.add(panel);

		buttonPane = new JPanel();
		buttonPane.setBackground(new Color(176, 196, 222));

		getContentPane().add(buttonPane, BorderLayout.EAST);

		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");

		GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
		gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
								.addComponent(cancelButton)
								.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(16)
						.addComponent(okButton)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cancelButton)
						.addContainerGap(19, Short.MAX_VALUE))
				);

		buttonPane.setLayout(gl_buttonPane);

		setVisible(true);		
		paintComponents(panel.getGraphics());
	}

	/**
	 * @return the geomLineAngle
	 */
	public Geometry getGeomLineAngle() {
		return geomLineAngle;
	}

	/**
	 * @param geomLineAngle the geomLineAngle to set
	 */
	public void setGeomLineAngle(Geometry geomLineAngle) {
		this.geomLineAngle = geomLineAngle;
	}
}

/**
 * create the panel for angle
 * 
 * @author Severino, Bruno de Oliveira
 * @version  1.0
 */
class PanelAngle extends JPanel{

	private static final long serialVersionUID = 1L;
	private ParallelLineWindow pWindow;
	private boolean snapLine = false;

	/**
	 * class constructor
	 * @param parallelLineWindow
	 */
	public PanelAngle( ParallelLineWindow parallelLineWindow) {
		setBounds(63, 42, 40, 40);
		setBackground(new Color(229, 235, 242));
		this.pWindow = parallelLineWindow;

		this.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();				
				Geometry polygon = new IntersectGeometry().getSnapBox(x, y, 2);				
				if(polygon.intersects(pWindow.getGeomLineAngle())){
					snapLine = true;
				} else {
					snapLine = false;
				}
			}


			@Override
			public void mouseDragged(MouseEvent e) {	
				if(snapLine){					
					double angle = catchAngle(pWindow.getGeomLineAngle(), e.getX(), e.getY());
					System.out.println(angle);
//					double interX1 = pWindow.getGeomLineAngle().getCoordinates()[0].x;
//					double interY1 = pWindow.getGeomLineAngle().getCoordinates()[0].y;
//					double interX2 = pWindow.getGeomLineAngle().getCoordinates()[1].x;
//					double interY2 = pWindow.getGeomLineAngle().getCoordinates()[1].y;			
//					double cad1 = (interX2 - interX1);
//					double cop1 = (interY2 - interY1);		
//					double hip1 = Math.sqrt((Math.pow(cad1, 2)+Math.pow(cop1, 2)));	
//					
//					double cad2 = (cad1*20)/hip1;
//					double cop2 = (cop1*20)/hip1;
//					
//					pWindow.getGeomLineAngle().getCoordinates()[1].x = cad2;
//					pWindow.getGeomLineAngle().getCoordinates()[1].y = cop2;
//					
//					
//					paintComponent(thisClass.getGraphics());
				}
			}
		});
	}	

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		update(g);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Circulo em Volta	
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(2));
		g2.drawOval(1, 1, 38, 38);

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1));
		g2.drawOval(0, 0, 40, 40);

		//Circulo Central
		g2.setColor(Color.BLACK);
		g2.drawOval(19, 19, 2, 2);
		g2.setColor(Color.WHITE);
		g2.fillOval(19, 19, 2, 2);

		//Linha
		Coordinate[] coords = pWindow.getGeomLineAngle().getCoordinates();
		int x1 = (int) coords[0].x;
		int y1 = (int) coords[0].y;
		int x2 = (int) coords[1].x;
		int y2 = (int) coords[1].y;

		g2.setColor(Color.BLUE);
		g2.drawLine(x1, y1, x2, y2);
	}

	/**
	 * Catch the angle, passing the geometry, x and y
	 * @param geom
	 * @param x
	 * @param y
	 * @return the angle
	 */
	private double catchAngle(Geometry geom, int x, int y) {
		double x1 = geom.getCoordinates()[0].x;
		double y1 = geom.getCoordinates()[0].y;
		double x2 = x;
		double y2 = y;			

		double cateto_ad = Math.abs(x2 - x1);
		double cateto_op = Math.abs(y2 - y1);	
		double hipotenusa = Math.sqrt((Math.pow(cateto_ad, 2)+Math.pow(cateto_op, 2)));		

		double radian = cateto_op / hipotenusa;

		double angle = Math.toDegrees(radian);

		if((x1 > x2 && y1 > y2) || (x1 < x2 && y1 < y2) || (x1 == x2 && y1 == y2)){			
			if(angle>45){
				radian = cateto_ad / hipotenusa;
				angle = (45-Math.toDegrees(radian)) +45;
			}
		}
		else{			
			angle = 180 - Math.toDegrees(radian);
			if(angle < 135){				
				radian = cateto_ad / hipotenusa;
				angle = Math.toDegrees(radian) +90;
				Math.abs(angle-180);
			}
			else if(angle > 180){
				System.out.println("Lol");
			}

		}
		return angle;
	}
}
