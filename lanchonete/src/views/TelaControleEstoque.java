package views;

import connection.Conexao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 *
 * @author Pablo
 */

/**
 * Classe responsável pela interface de cadastro de produtos
 * Permite inserir novos produtos e visualizar os produtos existentes
 */
public class TelaControleEstoque extends JFrame {
    // Componentes da interface gráfica
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton btnAtualizar; // Botão para atualizar a tabela
    private JButton btnEditar; // Botão para editar os produtos

    /**
     * Construtor da classe
     * Inicializa e configura a janela e seus componentes
     */
    public TelaControleEstoque() {
        // Configuração básica da janela
        setTitle("Cadastro de Produtos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Criação da tabela de produtos
        criarTabela();
        add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // Adiciona os botões "Atualizar" e "Editar"
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAtualizar = new JButton("Atualizar");
        painelBotoes.add(btnAtualizar);
        btnEditar = new JButton("Editar");
        painelBotoes.add(btnEditar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Adiciona um ActionListener ao botão "Atualizar"
        btnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarProdutos(); // Chama o método para recarregar os produtos
            }
        });

        // Adiciona um ActionListener ao botão "Editar"
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProduto(); // Chama o método para editar o produto
            }
        });

        // Carrega dados iniciais da tabela
        carregarProdutos();

        setVisible(true);
    }

    /**
     * Cria a tabela para exibição dos produtos
     */
    private void criarTabela() {
        modeloTabela = new DefaultTableModel(
            new String[]{"ID", "Nome", "Categoria", "Preço", "Estoque"}, 0
        );
        tabelaProdutos = new JTable(modeloTabela);
    }

    /**
     * Carrega os produtos do banco de dados e exibe na tabela
     */
    private void carregarProdutos() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar
        try (Connection con = Conexao.getConexao();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Produtos")) {

            // Adiciona cada produto encontrado na tabela
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("categoria"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ex.getMessage());
        }
    }

    /**
     * Edita um produto do banco de dados
     */
    private void editarProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.");
            return;
        }

        // Obtém os dados do produto selecionado
        int id = (int) modeloTabela.getValueAt(selectedRow, 0);
        String nome = (String) modeloTabela.getValueAt(selectedRow, 1);
        String categoria = (String) modeloTabela.getValueAt(selectedRow, 2);
        double preco = (double) modeloTabela.getValueAt(selectedRow, 3);
        int estoque = (int) modeloTabela.getValueAt(selectedRow, 4);

        // Solicita novos valores ao usuário
        String novoNome = JOptionPane.showInputDialog(this, "Nome:", nome);
        String novaCategoria = JOptionPane.showInputDialog(this, "Categoria:", categoria);
        String novoPrecoStr = JOptionPane.showInputDialog(this, "Preço:", preco);
        String novoEstoqueStr = JOptionPane.showInputDialog(this, "Estoque:", estoque);

        // Valida e atualiza os dados
        try {
            double novoPreco = Double.parseDouble(novoPrecoStr);
            int novoEstoque = Integer.parseInt(novoEstoqueStr);

            // Atualiza o banco de dados
            try (Connection con = Conexao.getConexao();
                 PreparedStatement pstmt = con.prepareStatement("UPDATE Produtos SET nome = ?, categoria = ?, preco = ?, estoque = ? WHERE id = ?")) {

                pstmt.setString(1, novoNome);
                pstmt.setString(2, novaCategoria);
                pstmt.setDouble(3, novoPreco);
                pstmt.setInt(4, novoEstoque);
                pstmt.setInt(5, id);

                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
                    carregarProdutos(); // Atualiza a tabela após a edição
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum produto encontrado com o ID informado.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço ou estoque inválidos. Insira apenas números.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o produto: " + ex.getMessage());
        }
    }
}