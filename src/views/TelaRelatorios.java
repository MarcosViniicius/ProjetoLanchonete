package views;
import dao.Conexao;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TelaRelatorios extends JFrame {
    private JTextArea areaRelatorio;

    public TelaRelatorios() {
        setTitle("Relatórios");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        areaRelatorio = new JTextArea();
        add(new JScrollPane(areaRelatorio), BorderLayout.CENTER);

        gerarRelatorio();
        setVisible(true);
    }

    private void gerarRelatorio() {
        try (Connection con = Conexao.getConexao()) {
            String sql = "SELECT Categoria, SUM(Estoque) as TotalEstoque FROM Produtos GROUP BY Categoria";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Categoria: ").append(rs.getString("Categoria"))
                  .append(" | Total em Estoque: ").append(rs.getInt("TotalEstoque"))
                  .append("\n");
            }
            areaRelatorio.setText(sb.toString());
        } catch (Exception ex) {
            areaRelatorio.setText("Erro: " + ex.getMessage());
        }
    }
}