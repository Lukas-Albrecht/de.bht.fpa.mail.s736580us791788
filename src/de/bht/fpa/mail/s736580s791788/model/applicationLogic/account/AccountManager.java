/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic.account;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import java.util.List;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class AccountManager implements AccountManagerIF {
    
    private final AccountDAOIF accountDB;
    
    public AccountManager(){
        //accountDB = new AccountFileDAO();
        accountDB = new AccountDBDAO();
    }

    @Override
    public Account getAccount(String name) {
        for(Account account: accountDB.getAllAccounts()){
            String accountName = account.getName();
            if(accountName.equals(name)){
                return account;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountDB.getAllAccounts();
    }

    @Override
    public boolean saveAccount(Account newAccount) {
        //check if name allready exists
        Account account = getAccount(newAccount.getName());
        //if null account with the same name doesnt exists
        if(account == null){
            accountDB.saveAccount(newAccount);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAccount(Account account) {
        return accountDB.updateAccount(account);
    }
    
}
