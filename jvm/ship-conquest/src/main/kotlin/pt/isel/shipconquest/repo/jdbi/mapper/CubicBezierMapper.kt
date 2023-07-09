package pt.isel.shipconquest.repo.jdbi.mapper

import com.example.shipconquest.repo.jdbi.GameRepositoryJDBI
import com.example.shipconquest.repo.jdbi.dbmodel.CubicBezierDBModel
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import org.postgresql.util.PGobject
import java.sql.ResultSet

class CubicBezierMapper: ColumnMapper<CubicBezierDBModel> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): CubicBezierDBModel {
        val obj = r.getObject(columnNumber, PGobject::class.java)
        return GameRepositoryJDBI.deserializeCubicBezier(obj.value ?: throw IllegalStateException())
    }
}
