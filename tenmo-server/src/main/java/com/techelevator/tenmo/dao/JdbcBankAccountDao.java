package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.BankAccount;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcBankAccountDao implements BankAccountDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcBankAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BankAccount findByUserId(Long id) {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()){
            return mapRowToBankAccount(rowSet);
        }
        throw new UsernameNotFoundException("User " + id + " was not found.");
    }

    @Override
    public BankAccount findByUserId(User user) {
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        if (rowSet.next()){
            return mapRowToBankAccount(rowSet);
        }
        throw new UsernameNotFoundException("User " + user.getId() + " was not found.");
    }

    @Override
    public boolean update(BankAccount account) {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
        try {
            jdbcTemplate.update(sql, account.getBalance(), account.getUserId());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

//    @Override
//    public boolean send(Long senderId, Long receiverId, BigDecimal amount) {
//        boolean success = false;
//        String sql = "SELECT * FROM account WHERE user_id = ?;";
//        SqlRowSet send = jdbcTemplate.queryForRowSet(sql, senderId);
//        SqlRowSet receive = jdbcTemplate.queryForRowSet(sql, receiverId);
//        if (send.next() && receive.next()) {
//            BankAccount sender = mapRowToBankAccount(send);
//            BankAccount receiver = mapRowToBankAccount(receive);
//            if (sender.getBalance().compareTo(amount) >= 0) {
//
//                sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
//                sender.setBalance(sender.getBalance().subtract(amount));
//                try {
//                    jdbcTemplate.update(sql, sender.getBalance(), sender.getId());
//                } catch (DataAccessException e) {
//                    return false;
//                }
//
//                sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
//                receiver.setBalance(receiver.getBalance().add(amount));
//                try {
//                    jdbcTemplate.update(sql, receiver.getBalance(), receiver.getId());
//                } catch (DataAccessException e) {
//                    return false;
//                }
//
//                // create transfer in table
//                sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) values (?, ?, ?, ?, ?)";
//                try {
//                    jdbcTemplate.update(sql, 2, 2, sender.getUserId(), receiver.getUserId(), amount);
//                    success = true;
//                } catch (DataAccessException e) {
//                    success = false;
//                }
//            }
//        }
//        return success;
//    }

    private BankAccount mapRowToBankAccount(SqlRowSet rs) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(rs.getLong("account_id"));
        bankAccount.setUserId(rs.getLong("user_id"));
        bankAccount.setBalance(rs.getBigDecimal("balance"));
        return bankAccount;
    }
}
