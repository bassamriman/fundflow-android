package com.rimanware.fundflow_android

import arrow.core.Option
import arrow.core.getOption
import fundflow.Fund
import fundflow.FundRef
import fundflow.ledgers.RecurrentTransaction
import fundflow.ledgers.RecurrentTransactionFundView
import fundflow.ledgers.RecurrentTransactionLedgerContext
import fundflow.ledgers.RecurrentTransactionLedgerContextAPI
import ledger.TransactionRef

object DataManager {
    private var recurrentTransactionLedgerContext = RecurrentTransactionLedgerContext.empty()
    private var fundViews: Map<FundRef, RecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }

    fun fundMap(): Map<FundRef, Fund> = recurrentTransactionLedgerContext.funds
    fun transactionMap(): Map<TransactionRef, RecurrentTransaction> =
        recurrentTransactionLedgerContext.recurrentTransactionLedger.transactions.map { it.transactionRef to it }
            .toMap()

    fun funds(): List<Fund> = fundMap().values.toList()
    fun getFundByRefId(id: String): Option<Fund> = getFundByRef(FundRef(id))
    fun getFundByRef(ref: FundRef): Option<Fund> = fundMap().getOption(ref)
    fun getRecurrentTransactionByRefId(id: String): Option<RecurrentTransaction> =
        getRecurrentTransactionByRef(TransactionRef(id))

    fun getRecurrentTransactionByRef(ref: TransactionRef): Option<RecurrentTransaction> =
        transactionMap().getOption(ref)

    fun fundView(ref: FundRef): Option<RecurrentTransactionFundView> = fundViews.getOption(ref)
    fun addFund(fund: Fund) {
        recurrentTransactionLedgerContext = RecurrentTransactionLedgerContextAPI.run {
            recurrentTransactionLedgerContext.addFund(fund)
        }
        fundViews = RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }
    }

    fun addRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        recurrentTransactionLedgerContext = RecurrentTransactionLedgerContextAPI.run {
            recurrentTransactionLedgerContext.addRecurrentTransaction(recurrentTransaction)
        }
        fundViews = RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }
    }


}