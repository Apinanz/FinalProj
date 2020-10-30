
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
public class PrimAl extends JFrame {
    
    ArrayList<Vertex> Vertexs = new ArrayList<>();
    ArrayList<Edge_> Edge_s = new ArrayList<>();
    
    JPanel menubar = new JPanel();
    int shift = 50;
    int start; //new
    int round = 0;
    double sumWeight = 0;
    Vertex v = null;
    AnsTable ansT;
    JTable table;
    JScrollPane tableScroll;
    Canvas c;
    JButton graphButt = new JButton("Graph");
    JButton tableButt = new JButton("Table");
    JButton nextButt = new JButton("Next");
    JPanel menu;
    
    Font sanSerifFont = new Font("SanSerif", Font.PLAIN, 24);

    //find size monitor
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    ArrayList<Edge_> T;
    ArrayList<Vertex> N, A;
    PrimAl(Vertex start,ArrayList<Vertex> Vertexs,ArrayList<Edge_> Edge_s){

        c = new Canvas() {
            @Override
            public void paint(Graphics g) {
                draw();
            }
        };
        c.setBackground(Color.white);
        T = new ArrayList<>();
        A = new ArrayList<>();
        N = new ArrayList<>();
        
        for (Vertex v : Vertexs) {
            A.add(v);
            this.Vertexs.add(v);
        }
        for(Edge_ t: Edge_s){
            this.Edge_s.add(t);
        }
        v = start;
        A.remove(v);
        N.add(v);
        round=0;
        ansT = new AnsTable(Vertexs);
        primAl();

        table = new JTable(ansT);
        table.setFont(sanSerifFont);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoscrolls(true);
        tableScroll = new JScrollPane(table);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setBounds(0,0,screenSize.width-400,screenSize.height);
//        frameAnswer.setLayout(null);
        
        nextButt = new JButton("Next");
        nextButt.setBounds((screenSize.width - getWidth()) - 400 + shift, 40, 300, 23);
        getContentPane().add(nextButt);
        nextButt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                primAl();
                
                table = new JTable(ansT);
                table.setFont(sanSerifFont);
                table.setRowHeight(30);
                table.getTableHeader().setReorderingAllowed(false);
                table.setAutoscrolls(true);
//                tableScroll = new JScrollPane(table);
//                tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//                tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//                tableScroll.setBounds(0,0,screenSize.width-400,screenSize.height);
                JScrollPane scroll = new JScrollPane(table);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setBounds(0,0,screenSize.width-400,screenSize.height);
                tableScroll.setViewportView(scroll);
                draw();
            }
        });
        
        graphButt = new JButton("Graph");
        graphButt.setBounds((screenSize.width - getWidth()) - 400 + shift, 100, 300, 23);
        getContentPane().add(graphButt);
        graphButt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graphButt.setVisible(false);
                graphButt.setVisible(true);
                tableScroll.setVisible(false);
                c.setVisible(true);
                
            }
        });
        
        tableButt = new JButton("Table");
        tableButt.setBounds((screenSize.width - getWidth()) - 400 + shift, 160, 300, 23);
        getContentPane().add(tableButt);
        tableButt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                tableScroll.setVisible(true);
                c.setVisible(false);
            }
        });
        c.setBounds(0, 0, (screenSize.width - getWidth()) - 400, (screenSize.height - getHeight()));
        menu = new JPanel();
        menu.setBackground(Color.cyan);
        menu.setBounds((screenSize.width - getWidth()) - 400, 0, 400, (screenSize.height - getHeight()));
        getContentPane().add(tableScroll);
        setBounds(0,0,screenSize.width,screenSize.height);
        add(c);
        add(menu);
        tableScroll.setVisible(false);
        //        g.setVisible(false);
        
        show();
    }
    
     void primAl() {
         v=v;
        double minWeight = Double.MAX_VALUE;
        ansT.setValueAt(v.name, round, 0);
        Vertex nextV = null;
        Edge_ minEdge = null;
        for (int i = 0; i < Vertexs.size(); i++) {
            Vertex u = Vertexs.get(i);
            Edge_ uv = null;
            for (Edge_ g : Edge_s) {
                if ((g.vertexA == v && g.vertexB == u) || (g.vertexA == u && g.vertexB == v)) {
                    uv = g;
                    break;
                }
            }
            String preCell = "" + '\u211E';
            if (round > 0) {
                preCell = ansT.getValueAt(round - 1, i + 1).toString();
            }
            if (uv != null && preCell.length() > 1) {
                double preWeight = Double.MAX_VALUE;
                if (round > 0) {
                    preWeight = Double.parseDouble(preCell.substring(0, preCell.indexOf(",")));
                }
                double weight = Double.parseDouble(uv.weight);
                if (weight < preWeight) {
                    ansT.setValueAt(weight + "," + u.name, round, i + 1);
                } else {
                    ansT.setValueAt(preCell, round, i + 1);
                }
            } else {
                if (N.contains(u)) {
                    ansT.setValueAt("-", i, i);
                } else {
                    ansT.setValueAt(preCell, i, i);
                }
            }
            String cell = ansT.getValueAt(round, i + 1).toString();
            if(cell.length()==1){
                continue;
            }
            double weight = Double.parseDouble(cell.substring(0, cell.indexOf(",")));
            if (weight < minWeight) {
                minWeight = weight;
                nextV = u;
                String nameV = cell.substring(cell.indexOf(",") + 1);
                Vertex w = null;
                for (Vertex a : A) {
                    if (a.name.equals(nameV)) {
                        w = a;
                        break;
                    }
                }
                for (Edge_ p : Edge_s) {
                    if ((p.vertexA == u && p.vertexB == w) || (p.vertexA == w && p.vertexB == u)) {
                        minEdge = p;
                        break;
                    }
                }
            }
        }
        N.add(nextV);
        A.remove(nextV);
        T.add(minEdge);
        v = nextV;
        sumWeight += minWeight;
        round++;
        ansT.fireTableDataChanged();
    }

    BufferedImage grid = null;

    public void draw() {

        Graphics2D g = (Graphics2D) c.getGraphics();
        g.setFont(sanSerifFont);

        if (grid == null) {
            grid = (BufferedImage) createImage(c.getWidth(), c.getHeight());
        }

        Graphics2D g2 = grid.createGraphics();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (Edge_ t : Edge_s) {
            t.draw(g2);
        }

        for (Vertex s : Vertexs) {
            s.draw(g2);
        }

        for (Edge_ t : Edge_s) {
            t.color = Color.BLACK;
        }
        // clear();

        g.drawImage(grid, null, 0, 0);
        //drawall

    }
    class AnsTable extends AbstractTableModel {

        String[] col;
        ArrayList<Object> row;

        AnsTable() {
            col = new String[1];
            col[0] = "N";
            row = new ArrayList<>();
        }

        AnsTable(ArrayList<Vertex> v) {
            row = new ArrayList<>();
            col = new String[v.size() + 1];
            col[0] = "N";
            for (int i = 0; i < v.size(); i++) {
                col[i + 1] = v.get(i).name;
            }
        }

        @Override
        public int getRowCount() {
            return row.size() / col.length;
        }

        @Override
        public int getColumnCount() {
            return col.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return this.row.get(getColumnCount() * row + col);
        }

        @Override
        public void setValueAt(Object data, int row, int col) {
            this.row.add(data);
            fireTableCellUpdated(row, col);
        }

        @Override
        public String getColumnName(int col) {
            return this.col[col];
        }

    }
    
}
