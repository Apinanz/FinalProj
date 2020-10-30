
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class ShowSolution extends JFrame {

    class Model extends AbstractTableModel {

        ArrayList<String> columnNames;
        LinkedList<Object> rowData;

        public Model(ArrayList<Vertex> Vertexs) {
            columnNames = new ArrayList<>();
            rowData = new LinkedList<>();
            columnNames.add("N");
            for (Vertex v : Vertexs) {
                columnNames.add(v.name);
            }
        }

        public Model() {
            columnNames = new ArrayList<>();
            rowData = new LinkedList<>();
            columnNames.add("N");
        }

        @Override
        public int getRowCount() {
            return rowData.size() / columnNames.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rowData.get(getColumnCount() * rowIndex + columnIndex);
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            rowData.addLast(value);
            fireTableCellUpdated(row, col);
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames.get(columnIndex);
        }

        public void deleteCurrentRow() {
            for (int i = 0; i < getColumnCount(); i++) {
                rowData.removeLast();
            }
            fireTableRowsDeleted(getRowCount(), getRowCount());
        }
    }
    int round = 0;
    double sumWeight = 0;
    JLabel answerLabel = new JLabel();
    Vertex start;
    JScrollPane scroll;
    Model model;
    JTable primTable;
    JButton graph = new JButton("Graph");
    JButton table = new JButton("Table");
    JButton next = new JButton("Next");
    JButton home = new JButton("Home");
    private ArrayList<Vertex> Vertexs = new ArrayList<>();
    private ArrayList<Edge_> Edge_s = new ArrayList<>();
    private LinkedList<Edge_> T = new LinkedList<>();
    private LinkedList<Vertex> A = new LinkedList<>();
    private LinkedList<Vertex> N = new LinkedList<>();
    private Vertex u;
    private Canvas c;
    Font sanSerifFont = new Font("SanSerif", Font.PLAIN, 24);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    JPanel menubar = new JPanel();
    int shift = 50;
    BufferedImage grid = null;

    public ShowSolution(ArrayList<Vertex> v, ArrayList<Edge_> e,Vertex start) {
        super("canvas");
        c = new Canvas() {
            @Override
            public void paint(Graphics g) {
                draw();
            }
        };
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        for (Edge_ edge : e) {
            Edge_s.add(edge);
        }
        for (Vertex vertex : v) {
            Vertexs.add(vertex);
            A.add(vertex);
        }
        this.start = start;
        model = new Model(Vertexs);
        answerLabel.setFont(sanSerifFont);
        String text = "<html>";
        text += "<CENTER>Total weight of</CENTER>";
        text += "<CENTER>minimum spanning</CENTER>";
        text += "<CENTER>tree is : " + sumWeight + "</CENTER>";
        answerLabel.setText(text);
        answerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        answerLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane answer = new JScrollPane(answerLabel);
        answer.setBounds((screenSize.width - getWidth()) - 400 + shift, 340, 300, 23);
        answer.setSize(new Dimension(300, screenSize.height - 540));
        getContentPane().add(answer);
        primTable = new JTable(model) {
            DefaultTableCellRenderer renderCenter = new DefaultTableCellRenderer();

            {
                renderCenter.setHorizontalAlignment(SwingConstants.CENTER);
            }

            @Override
            public TableCellRenderer getCellRenderer(int arg0, int arg1) {
                return renderCenter;
            }
        };
        primTable.setRowHeight(30);
        primTable.setFont(sanSerifFont);
        primTable.getTableHeader().setReorderingAllowed(false);
        primTable.setAutoscrolls(true);
        scroll = new JScrollPane(primTable);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(0, 0, 150, 150);
        scroll.setSize(new Dimension(screenSize.width - 400, screenSize.height - getHeight()));
        getContentPane().add(scroll);
        scroll.setVisible(false);
        Dimension di = getPreferredSize();

        graph.setFont(sanSerifFont);
        graph.setBounds((screenSize.width - getWidth()) - 400 + shift, 100, 300, 23);
        getContentPane().add(graph);
        graph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                graph.setVisible(false);
                graph.setVisible(true);
                scroll.setVisible(false);
                c.setVisible(true);
                draw();
            }
        });
        table.setFont(sanSerifFont);
        table.setBounds((screenSize.width - getWidth()) - 400 + shift, 160, 300, 23);
        getContentPane().add(table);
        table.addActionListener(this::tableButtAction);

        home.setFont(sanSerifFont);
        home.setBounds((screenSize.width - getWidth()) - 400 + shift, 60, 300, 23);
        getContentPane().add(home);
        home.addActionListener(this::homeButtAction);

        next.setFont(sanSerifFont);
        next.setBounds((screenSize.width - getWidth()) - 400 + shift, 280, 300, 23);
        getContentPane().add(next);
        next.addActionListener((ActionEvent e1) -> {
        primAlgorithm();

        JTable tables = new JTable(model);
        tables.setFont(sanSerifFont);
        tables.setRowHeight(30);
        tables.getTableHeader().setReorderingAllowed(false);
        tables.setAutoscrolls(true);
        JScrollPane scrolls = new JScrollPane(tables);
        scrolls.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrolls.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrolls.setBounds(0, 0, screenSize.width - 400, screenSize.height);
        scroll.setViewportView(scrolls);
        draw();
        });
        c.setBackground(Color.white);
        menubar.setBackground(Color.cyan);
        menubar.setBounds((screenSize.width - getWidth()) - 400, 0, 400, (screenSize.height - getHeight()));
        c.setBounds(0, 0, (screenSize.width - getWidth()) - 400, (screenSize.height - getHeight()));
        setBounds(0, 0, (screenSize.width - getWidth()), (screenSize.height - getHeight()));
        add(c);
        add(menubar);
        show();
    }
    
    void homeButtAction(ActionEvent e){
        GraphDrawing gui = new  GraphDrawing();
        for(Edge_ r : Edge_s){
            r.isSelect = false;
        }
        
        for(Vertex k  : Vertexs){
            k.isSelect = false;
        }
        gui.backToDisplay(Vertexs, Edge_s);
        setVisible(false);
    }

    public void primAlgorithm() {
        if (round == Vertexs.size() - 1) {
            return;
        }
        if (round == 0) {
            u = start;
            N.addLast(u);
            A.remove(u);
        }
        Edge_ uw = null;
        Vertex v = null;
        model.setValueAt(u.name, round, 0);
        double minWeight = Double.MAX_VALUE;
        for (int j = 0; j < Vertexs.size(); j++) {
            Edge_ ed = null;
            Vertex w = Vertexs.get(j);
            for (Edge_ t : Edge_s) {
                if (((t.vertexA == u && t.vertexB == w) || (t.vertexA == w && t.vertexB == u))&&!T.contains(t)) {
                    ed = t;
                    break;
                }
            }
            if (N.contains(w)) {
                model.setValueAt("-", round, j + 1);
            } else if (ed!=null) {
                double weight = Double.parseDouble(ed.weight);
                double preWeight = Double.MAX_VALUE;
                if (round > 0 && model.getValueAt(round - 1, j + 1).toString().length() > 1) {
                    String upper = model.getValueAt(round - 1, j + 1).toString();
                    String prev = upper.substring(0, upper.indexOf(","));
                    preWeight = Double.parseDouble(prev);
                }
                if ((round > 0 && model.getValueAt(round - 1, j + 1).equals(Character.toString('\u221E')))
                        || weight < preWeight) {
                    model.setValueAt(weight + ", " + u.name, round, j + 1);
                } else {
                    model.setValueAt(model.getValueAt(round - 1, j + 1), round, j + 1);
                }
            } else {
                if (round > 0) {
                    model.setValueAt(model.getValueAt(round - 1, j + 1), round, j + 1);
                } else {
                    model.setValueAt(Character.toString('\u221E'), round, j + 1);
                }
            }
            if (model.getValueAt(round, j + 1).toString().length()==1) {
                continue;
            }
            String currentSlot = model.getValueAt(round, j + 1).toString();
            String current = currentSlot.substring(0, currentSlot.indexOf(","));
            double currentWeight = Double.parseDouble(current);
            if (currentWeight < minWeight) {
                minWeight = currentWeight;
                v = w;
                String target = currentSlot.substring(currentSlot.indexOf(",")+2);
                for(Edge_ e : Edge_s){
                    if(!T.contains(e)&&((e.vertexA.name.equals(target)&&e.vertexB.name.equals(w.name))||
                                        (e.vertexB.name.equals(target)&&e.vertexA.name.equals(w.name)))){
                        uw = e;
                        break;
                    }
                }
            }
        }
        sumWeight += minWeight;
        if (uw != null) {

            uw.isSelect = true;
        }
        u = v;
        A.remove(v);
        N.addLast(v);
        T.addLast(uw);
        round++;
        String text = "<html>";
        text += "<CENTER>Total weight of</CENTER>";
        text += "<CENTER>minimum spanning</CENTER>";
        text += "<CENTER>tree is : " + sumWeight + "</CENTER>";
        answerLabel.setText(text);
        model.fireTableDataChanged();
        draw();
    }

    public void graphButtAction(ActionEvent ae) {
        graph.setVisible(false);
        graph.setVisible(true);
        c.setVisible(true);
        scroll.setVisible(false);
        draw();
    }

    public void tableButtAction(ActionEvent ae) {
        Graphics2D g = (Graphics2D) c.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        scroll.setVisible(true);
        c.setVisible(false);
    }

    public void nextButtAction(ActionEvent ae) {
        primAlgorithm();
    }

    public void draw() {
        Graphics2D g = (Graphics2D) c.getGraphics();
        g.setFont(sanSerifFont);
        if (grid == null) {
            grid = (BufferedImage) createImage(c.getWidth(), c.getHeight());
        }
        Graphics2D g2 = grid.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        Edge_s.forEach((t) -> {
            t.draw(g2);
        });
        Vertexs.forEach((s) -> {
            s.draw(g2);
        });
        g.drawImage(grid, null, 0, 0);
    }
}
