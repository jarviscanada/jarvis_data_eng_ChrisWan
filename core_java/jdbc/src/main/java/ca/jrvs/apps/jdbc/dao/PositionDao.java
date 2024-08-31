package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Position;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

    private Connection c;
    private static final String GET_ALL = "SELECT * FROM position";
    private static final String GET_ONE = "SELECT symbol, number_of_shares, "
            + "value_paid FROM position WHERE symbol = ?";
    private static final String DELETE_ONE = "DELETE FROM position WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM position";
    private static final String INSERT = "INSERT INTO position (symbol, "
            + "number_of_shares, value_paid) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE position SET number_of_shares = ?, "
            + "value_paid = ? WHERE symbol = ?";

    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    public PositionDao(Connection connection) {
        this.c = connection;
    }

    @Override
    public Position save(Position entity) throws IllegalArgumentException {
        infoLogger.info("Saving Position entity");
        if (this.findById(entity.getTicker()).isEmpty()) {
            try(PreparedStatement statement = this.c.prepareStatement(INSERT)){
                infoLogger.info("Inserting the new position");
                statement.setString(1, entity.getTicker());
                statement.setDouble(2, entity.getNumOfShares());
                statement.setDouble(3, entity.getValuePaid());
                statement.execute();
                return entity;
            }catch(SQLException e){
                errorLogger.error("An SQL Exception had occurred when trying to INSERT a new position on {}", e.getMessage());
            }
        } else {
            try(PreparedStatement statement = this.c.prepareStatement(UPDATE)){
                infoLogger.info("Updating the position values");
                statement.setDouble(1, entity.getNumOfShares());
                statement.setDouble(2, entity.getValuePaid());
                statement.setString(3, entity.getTicker());
                statement.execute();
                return entity;
            }catch(SQLException e){
                errorLogger.error("An SQL Exception had occurred when trying to UPDATE a position on {}", e.getMessage());
            }
        }
        return entity;
    }

    @Override
    public Optional<Position> findById(String s) throws IllegalArgumentException {
        Position position = new Position();
        try(PreparedStatement statement = this.c.prepareStatement(GET_ONE)){
            infoLogger.info("Getting one position by Id");
            statement.setString(1, s);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));
                return Optional.of(position);
            } else{
                return Optional.empty();
            }
        } catch (SQLException e){
            errorLogger.error("An SQL Exception had occurred when trying to SELECT a position based on Id, error message {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        try (PreparedStatement statement = this.c.prepareStatement(GET_ALL)) {
            infoLogger.info("SELECTING all positions from DB");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Position position = new Position();
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));
                positions.add(position);
            }

            return positions;

        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to SELECT all on position, error message {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteById(String s) throws IllegalArgumentException {
        try(PreparedStatement statement = this.c.prepareStatement(DELETE_ONE)) {
            infoLogger.info("DELETING a position based off Id");
            statement.setString(1, s);
            statement.execute();
        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to DELETE a position based on Id, error message {}", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try(PreparedStatement statement = this.c.prepareStatement(DELETE_ALL)) {
            infoLogger.info("DELETING all positions");
            statement.execute();
        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to DELETE all positions, error message {}", e.getMessage());
        }
    }

}