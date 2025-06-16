package views;

import connection.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class TelaRelatorios extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable tabelaPedidos;
    private JTable tabelaEstoque;
    
    public TelaRelatorios() {
        // Configurações básicas da janela
        setTitle("Relatórios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Inicializa o TabbedPane
        tabbedPane = new JTabbedPane();
        
        // Cria as tabelas
        criarTabelaPedidos();
        criarTabelaEstoque();
        
        // Adiciona as abas
        tabbedPane.addTab("Pedidos", new JScrollPane(tabelaPedidos));
        tabbedPane.addTab("Estoque", new JScrollPane(tabelaEstoque));
        
        // Adiciona o TabbedPane ao frame
        add(tabbedPane);
        
        // Atualiza os dados das tabelas
        atualizarTabelaPedidos();
        atualizarTabelaEstoque();
    }
    
    private void criarTabelaPedidos() {
        String[] colunas = {"ID", "Cliente", "Produto", "Quantidade", "Valor Total", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        tabelaPedidos = new JTable(modelo);
    }
    
    private void criarTabelaEstoque() {
        String[] colunas = {"Código", "Produto", "Quantidade", "Preço Unitário", "Valor Total"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaEstoque = new JTable(modelo);
        tabelaEstoque.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabelaEstoque.getColumnModel().getColumn(1).setPreferredWidth(200);
    }
    
    private void atualizarTabelaPedidos() {
        DefaultTableModel modelo = (DefaultTableModel) tabelaPedidos.getModel();
        modelo.setRowCount(0); // Limpa a tabela
        
        List<String[]> dados = listarPedidos();
        for (String[] linha : dados) {
            modelo.addRow(linha);
        }
    }
    
    private void atualizarTabelaEstoque() {
        DefaultTableModel modelo = (DefaultTableModel) tabelaEstoque.getModel();
        modelo.setRowCount(0); // Limpa a tabela
        
        List<String[]> dados = listarEstoque();
        for (String[] linha : dados) {
            modelo.addRow(linha);
        }
    }

    public List<String[]> listarPedidos() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
            SELECT p.ID, c.Nome, pr.Nome, p.Quantidade, p.Valor_Total, p.Status
            FROM Pedidos p
            JOIN Clientes c ON p.ID_Cliente = c.ID
            JOIN Produtos pr ON p.ID_Produto = pr.ID
        """;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<String[]> listarEstoque() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
            SELECT 
                ID,
                Nome,
                Categoria,
                Preco,
                Estoque,
                (Estoque * Preco) as Valor_Total
            FROM produtos
            WHERE Estoque > 0
            ORDER BY Nome
        """;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("ID"),
                    rs.getString("Nome"),
                    rs.getString("Estoque"),
                    String.format("R$ %.2f", rs.getDouble("Preco")),
                    String.format("R$ %.2f", rs.getDouble("Valor_Total"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados do estoque: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return lista;
    }
}