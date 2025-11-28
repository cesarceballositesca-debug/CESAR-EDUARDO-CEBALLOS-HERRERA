import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    private Font font;
    private JPanel panelArbol;
    private JTextField texto;
    private RBT<Integer> rbt = new RBT<>();
      // Mapa de posiciones como atributo
    private Map<Nodo<Integer>, Point> posiciones = new HashMap<>();

    public Main() {
        super("graficar RBT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.BLUE);
        setLocationRelativeTo(null);
        font = new Font("Arial", Font.BOLD, 40);

        // Panel para graficar el árbol
        panelArbol = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarArbol((Graphics2D) g);
            }
        };/*En esta parte lo cambie a rosa para hacerlo mas vistoso */
        panelArbol.setBackground(Color.PINK);
        this.add(panelArbol, BorderLayout.CENTER);

           //Botones de insertar y area de texto
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        this.add(panel, BorderLayout.NORTH);

        texto = new JTextField(40);
        texto.setFont(font);
        texto.setHorizontalAlignment(JTextField.CENTER);

        JButton botonInsertar = new JButton("Insertar");
        botonInsertar.setFont(font);

        JButton botonBorrar = new JButton("Eliminar");
        botonBorrar.setFont(font);

        panel.add(texto);
        panel.add(botonInsertar);
        panel.add(botonBorrar);

        // Acción insertar
        botonInsertar.addActionListener(e -> {
            try {
                int value = Integer.parseInt(texto.getText().trim());
                rbt.insert(value);
                recalcularPosiciones();
                panelArbol.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa un número entero válido.");
            }
        });

        // Acción eliminar
        botonBorrar.addActionListener(e -> {
            try {
                int value = Integer.parseInt(texto.getText().trim());
                rbt.delete(value);
                recalcularPosiciones();
                panelArbol.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa un número entero válido.");
            }
        });

        setVisible(true);
    }

    private void recalcularPosiciones() {
        int ancho = panelArbol.getWidth();
        int alto = panelArbol.getHeight();
        if (ancho <= 0 || alto <= 0) {
            ancho = 1000;
            alto = 600;
        }

        posiciones.clear();

        int n = Math.max(rbt.size, 1);
        int aa = (int) Math.ceil(Math.log(n) / Math.log(2));
        aa = Math.max(aa, 1) + 2;

        int xCentro = ancho / 2;
        calcularPosiciones(rbt.root, 1, ancho, alto, xCentro, aa);
    }

    public void calcularPosiciones(Nodo<Integer> nodo, int nivel,
                                   int ancho, int alto, int x, int aa) {
        if (nodo == null) return;

        int distancia = (ancho / (nivel + 1)) / 2; // espacio horizontal
        int y = alto / aa * nivel;                 // espacio vertical

        posiciones.put(nodo, new Point(x, y));

        calcularPosiciones(nodo.left, nivel + 1, ancho, alto, x - distancia, aa);
        calcularPosiciones(nodo.right, nivel + 1, ancho, alto, x + distancia, aa);
    }

    private void dibujarArbol(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diameter = 80;

        // Primero dibujar las ramas 
        for (Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point p = entry.getValue();

            if (nodo.left != null && posiciones.containsKey(nodo.left)) {
                Point pH = posiciones.get(nodo.left);
                g2.setColor(Color.BLACK);
                g2.drawLine(p.x, p.y, pH.x, pH.y);
            }
            if (nodo.right != null && posiciones.containsKey(nodo.right)) {
                Point pH = posiciones.get(nodo.right);
                g2.setColor(Color.BLACK);
                g2.drawLine(p.x, p.y, pH.x, pH.y);
            }
        }

        for (Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point p = entry.getValue();

            int x = p.x - diameter / 2;
            int y = p.y - diameter / 2;

            // Negro se ve GRIS, rojo se ve ROJO
            Color fillColor = (nodo.color == Color.BLACK) ? Color.GRAY : Color.RED;
            g2.setColor(fillColor);
            g2.fillOval(x, y, diameter, diameter);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x, y, diameter, diameter);

            g2.setFont(font);
            String text = "" + nodo.elemento;
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (diameter - fm.stringWidth(text)) / 2;
            int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

