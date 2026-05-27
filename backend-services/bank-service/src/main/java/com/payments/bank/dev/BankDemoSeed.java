package com.payments.bank.dev;

import com.payments.bank.config.BankProperties;
import com.payments.bank.domain.AccountEntity;
import com.payments.bank.repo.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Demo accounts for educational simulation only.
 */
@Component
public class BankDemoSeed implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BankDemoSeed.class);

    public static final UUID CLEARING_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1");
    public static final UUID USER_NIVEDITA_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2");
    public static final UUID USER_ROHAN_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3");

    public static final UUID CLEARING_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1");
    public static final UUID USER_ROHAN_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2");

    private final AccountRepository accounts;
    private final BankProperties props;

    public BankDemoSeed(AccountRepository accounts, BankProperties props) {
        this.accounts = accounts;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (accounts.count() > 0) {
            return;
        }
        String code = props.code();
        if ("A".equalsIgnoreCase(code)) {
            accounts.save(new AccountEntity(CLEARING_A, null, AccountEntity.Kind.CLEARING, 0, "Clearing A"));
            accounts.save(new AccountEntity(USER_NIVEDITA_A, "nivedita@banka", AccountEntity.Kind.USER, 500_000L, "Nivedita"));
            accounts.save(new AccountEntity(USER_ROHAN_A, "rohan@banka", AccountEntity.Kind.USER, 100_000L, "Rohan A"));
            log.info("Seeded Bank A demo VPAs: nivedita@banka, rohan@banka");
        } else if ("B".equalsIgnoreCase(code)) {
            accounts.save(new AccountEntity(CLEARING_B, null, AccountEntity.Kind.CLEARING, 0, "Clearing B"));
            accounts.save(new AccountEntity(USER_ROHAN_B, "rohan@bankb", AccountEntity.Kind.USER, 100_000L, "Rohan B"));
            log.info("Seeded Bank B demo VPA: rohan@bankb");
        }
    }
}
