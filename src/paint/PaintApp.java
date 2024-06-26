package paint;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PaintApp extends JFrame {

    private JPanel drawingArea;
    private JComboBox<Integer> sidesComboBox;
    private Color selectedColor = Color.BLACK;
    private List<Shape> shapes = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();
    private int sides = 3; // Número de lados predeterminado
    private int startX, startY, endX, endY;
    private JRadioButton circleRadioButton;
    private JRadioButton lineRadioButton;
    private JRadioButton polygonRadioButton;

    public PaintApp() {
        setTitle("Paint App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicialización de componentes
        drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Establecer color de fondo blanco
                setBackground(Color.WHITE);
                g.setColor(selectedColor);
                for (Shape shape : shapes) {
                    g.drawPolygon(shape.xPoints, shape.yPoints, shape.sides);
                }
                for (Circle circle : circles) {
                    g.drawOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);
                }
                if (lineRadioButton.isSelected()) {
                    for (Line line : lines) {
                        g.drawLine(line.startX, line.startY, line.endX, line.endY);
                    }
                }
                if (sides == 0 && !circles.isEmpty()) {
                    // Dibujar círculo interactivamente mientras se arrastra el mouse
                    Circle circle = circles.get(circles.size() - 1);
                    g.drawOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);
                } else if (sides > 2 && startX != endX && startY != endY) {
                    // Dibujar polígono regular mientras se arrastra el mouse
                    int centerX = (startX + endX) / 2;
                    int centerY = (startY + endY) / 2;
                    int radius = Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 2;
                    double angleIncrement = 2 * Math.PI / sides;
                    int[] xPoints = new int[sides];
                    int[] yPoints = new int[sides];
                    for (int i = 0; i < sides; i++) {
                        xPoints[i] = (int) (centerX + radius * Math.cos(i * angleIncrement));
                        yPoints[i] = (int) (centerY + radius * Math.sin(i * angleIncrement));
                    }
                    g.drawPolygon(xPoints, yPoints, sides);
                }
            }
        };
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                endX = startX;
                endY = startY;
                drawingArea.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (sides > 2) {
                    shapes.add(new Shape(sides, startX, startY, endX, endY));
                } else if (sides == 0 && circles.isEmpty()) {
                    int radius = Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 2;
                    circles.add(new Circle(startX - radius, startY - radius, radius));
                }
                if (lineRadioButton.isSelected()) {
                    if (lines.isEmpty()) {
                        lines.add(new Line(startX, startY, endX, endY));
                    } else {
                        Line lastLine = lines.get(lines.size() - 1);
                        if (!(lastLine.startX == startX && lastLine.startY == startY && lastLine.endX == endX && lastLine.endY == endY)) {
                            lines.add(new Line(startX, startY, endX, endY));
                        }
                    }
                }
                drawingArea.repaint();
            }
        });

        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                if (sides == 0 && !circles.isEmpty()) {
                    Circle circle = circles.get(circles.size() - 1);
                    circle.radius = Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 2;
                }
                drawingArea.repaint();
            }
        });

        sidesComboBox = new JComboBox<>();
        for (int i = 3; i <= 19; i++) {
            sidesComboBox.addItem(i);
        }
        sidesComboBox.setSelectedIndex(0); // Selección predeterminada

        circleRadioButton = new JRadioButton("Dibujar Círculo");
        lineRadioButton = new JRadioButton("Dibujar Línea");
        polygonRadioButton = new JRadioButton("Dibujar Polígono");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(circleRadioButton);
        buttonGroup.add(lineRadioButton);
        buttonGroup.add(polygonRadioButton);

        circleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (circleRadioButton.isSelected()) {
                    sides = 100; // Establecer el número de lados como 100 para dibujar un círculo
                    lineRadioButton.setSelected(false);
                    polygonRadioButton.setSelected(false);
                }
                drawingArea.repaint();
            }
        });

        lineRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lineRadioButton.isSelected()) {
                    sides = 2; // Establecer el número de lados como 2 para dibujar una línea
                    circleRadioButton.setSelected(false);
                    polygonRadioButton.setSelected(false);
                }
                drawingArea.repaint();
            }
        });

        polygonRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (polygonRadioButton.isSelected()) {
                    sides = (int) sidesComboBox.getSelectedItem(); // Establecer el número de lados según la selección del combo box
                    circleRadioButton.setSelected(false);
                    lineRadioButton.setSelected(false);
                }
                drawingArea.repaint();
            }
        });

        // Organización de la interfaz
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(new JLabel("Selecciona figura:"));
        controlsPanel.add(circleRadioButton);
        controlsPanel.add(lineRadioButton);
        controlsPanel.add(polygonRadioButton);
        controlsPanel.add(new JLabel("Selecciona lados:"));
        controlsPanel.add(sidesComboBox);
        controlsPanel.add(new JLabel("Select Color:"));
        JButton colorButton = new JButton("Change Color");
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(PaintApp.this, "Choose Color", selectedColor);
                if (newColor != null) {
                    selectedColor = newColor;
                }
            }
        });
        controlsPanel.add(colorButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controlsPanel, BorderLayout.NORTH);
        contentPane.add(drawingArea, BorderLayout.CENTER);
    }

    class Shape {
        int sides;
        int[] xPoints;
        int[] yPoints;

        public Shape(int sides, int startX, int startY, int endX, int endY) {
            this.sides = sides;
            int centerX = (startX + endX) / 2;
            int centerY = (startY + endY) / 2;
            int radius = Math.min(Math.abs(startX - endX), Math.abs(startY - endY)) / 2;
            double angleIncrement = 2 * Math.PI / sides;
            xPoints = new int[sides];
            yPoints = new int[sides];
            for (int i = 0; i < sides; i++) {
                xPoints[i] = (int) (centerX + radius * Math.cos(i * angleIncrement));
                yPoints[i] = (int) (centerY + radius * Math.sin(i * angleIncrement));
            }
        }
    }

    class Circle {
        int x, y, radius;

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    class Line {
        int startX, startY, endX, endY;

        public Line(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PaintApp().setVisible(true);
            }
        });
    }
}
