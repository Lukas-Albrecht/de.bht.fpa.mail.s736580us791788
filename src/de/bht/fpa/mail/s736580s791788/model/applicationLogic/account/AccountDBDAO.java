/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic.account;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Lukas Albrecht, Peter Albrecht
 */
public class AccountDBDAO implements AccountDAOIF{
    
    EntityManagerFactory emf;
    
    public AccountDBDAO() {
        //create accounts in database
        TestDBDataProvider.createAccounts();  
        this.emf = Persistence.createEntityManagerFactory("fpa");
    }

    @Override
    public List<Account> getAllAccounts() {
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createQuery("SELECT accounts FROM Account accounts ");
        List<Account> list = query.getResultList();
        
        em.close();
        return list;
    }

    @Override
    public Account saveAccount(Account account) {
        EntityManager em = emf.createEntityManager();
        
        //changes appear in only one transaction for integrity of data ("rollback")
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        em.persist(account);
        trans.commit();
        
        em.close();
        return account;
    }

    @Override
    //currently not used!!!!!!!
    public boolean updateAccount(Account acc) {
        EntityManager em = emf.createEntityManager();
        
        //changes appear in only one transaction for integrity of data ("rollback")
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        em.merge(acc);
        trans.commit();
        
        em.close();
        return true; //!!!!!!!suboptimal!!!!!!!!!
    }
    
}
