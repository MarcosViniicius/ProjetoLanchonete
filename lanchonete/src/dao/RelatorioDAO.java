package dao;

import connection.Conexao;
import java.sql.*;
import java.util.*;

public class RelatorioDAO {

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
            SELECT pr.Nome, e.Quantidade_Entrada, e.Quantidade_Saida
            FROM Estoque e
            JOIN Produtos pr ON e.ID_Produto = pr.ID
        """;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int saldo = rs.getInt(2) - rs.getInt(3);
                lista.add(new String[]{
                    rs.getString(1),
                    String.valueOf(rs.getInt(2)),
                    String.valueOf(rs.getInt(3)),
                    String.valueOf(saldo)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}