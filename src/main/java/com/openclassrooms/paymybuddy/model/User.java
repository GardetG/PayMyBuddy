package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Model Class of a user with credentials such as email and password and personal information,
 * firstname, lastname, list of bank accounts and list of connections with other
 * users.
 * This class extends ComptableEntity to provide a balance.
 */

@Entity
@Table(name = "user")
public class User extends ComptableEntity implements UserDetails {

  private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

  private User() {
    super(
        ApplicationValue.USER_BALANCE_CEILING
    );
  }

  /**
   * User parametric constructor with required value to instantiate a valid user.
   *
   * @param firstname of user
   * @param lastname of user
   * @param email of user
   * @param password of user
   * @param role authorization of the user
   */
  public User(String firstname, String lastname, String email, String password, Role role,
              LocalDateTime registrationDate) {
    super(
        ApplicationValue.INITIAL_USER_BALANCE,
        ApplicationValue.USER_BALANCE_CEILING
    );
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.password = password;
    this.role = role;
    this.registrationDate = registrationDate;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  @Getter @Setter
  private int userId;

  @Column(name = "firstname")
  @Getter @Setter
  private String firstname;

  @Column(name = "lastname")
  @Getter @Setter
  private String lastname;

  @Column(name = "email", unique = true)
  @Getter @Setter
  private String email;

  @Column(name = "password")
  @Getter @Setter
  private String password;

  @Column(name = "role")
  @Getter @Setter
  private Role role;

  @Column(name = "registration_date")
  @Getter
  private LocalDateTime registrationDate;

  @Column(name = "enabled")
  @Getter @Setter
  private boolean enabled = true;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private final Set<BankAccount> bankAccounts = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "connection",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "connection_id"))
  private final Set<User> connections = new HashSet<>();

  public Set<BankAccount> getBankAccounts() {
    return Collections.unmodifiableSet(bankAccounts);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return firstname.equals(user.firstname)
        && lastname.equals(user.lastname)
        && email.equals(user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstname, lastname, email);
  }

  /**
   * Add a bank account to user if it hasn't been added yet.
   *
   * @param bankAccount to add
   * @throws ResourceAlreadyExistsException if already added
   */
  public void addBankAccount(BankAccount bankAccount) throws ResourceAlreadyExistsException {
    if (bankAccounts.contains(bankAccount)) {
      LOGGER.error("This bank account already exists for user {}", userId);
      throw new ResourceAlreadyExistsException("This bank account already exists");
    }
    bankAccounts.add(bankAccount);
    bankAccount.setUser(this);
  }

  /**
   * Remove a bank account from user if it exists.
   *
   * @param bankAccount to remove
   * @throws ResourceNotFoundException if not exists
   */
  public void removeBankAccount(BankAccount bankAccount) throws ResourceNotFoundException {
    if (!bankAccounts.contains(bankAccount)) {
      LOGGER.error("This bank account is not found for user {}", userId);
      throw new ResourceNotFoundException("This bank account is not found");
    }
    bankAccounts.remove(bankAccount);
    bankAccount.setUser(null);
  }

  public Set<User> getConnections() {
    return Collections.unmodifiableSet(connections);
  }

  /**
   * Add a connection to user if it hasn't been added yet.
   *
   * @param connection to add
   * @throws ResourceAlreadyExistsException if already added
   */
  public void addConnection(User connection) throws ResourceAlreadyExistsException {
    if (connections.contains(connection)) {
      LOGGER.error("This connection already exists for user {}", userId);
      throw new ResourceAlreadyExistsException("This connection already exists");
    }
    connections.add(connection);
    connection.connections.add(this);
  }

  /**
   * Remove a connection from user if it exists.
   *
   * @param connection to remove
   * @throws ResourceNotFoundException if not exists
   */
  public void removeConnection(User connection) throws ResourceNotFoundException {
    if (!connections.contains(connection)) {
      LOGGER.error("This connection is not found for user {}", userId);
      throw new ResourceNotFoundException("This connection is not found");
    }
    connections.remove(connection);
    connection.connections.remove(this);
  }

  /**
   * Clear all connections of the user.
   */
  public void clearConnection() {
    connections.forEach(connection -> connection.connections.remove(this));
    connections.clear();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    return authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

}