package pt.isel.shipconquest.repo.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.context.annotation.Configuration
import pt.isel.shipconquest.repo.Transaction
import pt.isel.shipconquest.repo.TransactionManager

@Configuration
class TransactionManagerJDBI(private val jdbi: Jdbi): TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            handle.setTransactionIsolation(TransactionIsolationLevel.REPEATABLE_READ)
            val transaction = TransactionJDBI(handle)
            block(transaction)
        }
}