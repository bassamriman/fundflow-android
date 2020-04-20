package com.rimanware.fundflow_android

import arrow.core.Option
import arrow.core.getOption
import fundflow.Fund
import fundflow.FundRef
import fundflow.ledgers.*
import ledger.LedgerContextAPI
import ledger.TransactionRef
import java.time.LocalDateTime

object DataManager {
    var recurrentTransactionLedgerContext = RecurrentTransactionLedgerContext.empty()
    var fundViews: Map<FundRef, RecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }

    //Fund
    private fun fundMap(): Map<FundRef, Fund> = recurrentTransactionLedgerContext.funds
    fun loadAllFunds(): List<Fund> = fundMap().values.toList()

    fun loadFundUsingRefId(id: String): Option<Fund> = loadFundUsingRef(FundRef(id))
    fun loadFundUsingRef(ref: FundRef): Option<Fund> = fundMap().getOption(ref)
    fun loadFundView(ref: FundRef): Option<RecurrentTransactionFundView> = fundViews.getOption(ref)

    fun loadFundFlowView(
        ref: FundRef,
        dateTime: LocalDateTime
    ): Option<CombinableRecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run {
            LedgerContextAPI.run {
                val a = recurrentTransactionLedgerContext.flowAt(dateTime)
                    .view(ref, CombinableRecurrentTransactionFundViewFactory)
                a
            }
        }

    fun saveFund(fund: Fund) {
        recurrentTransactionLedgerContext = RecurrentTransactionLedgerContextAPI.run {
            recurrentTransactionLedgerContext.addFund(fund)
        }
        fundViews =
            RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }
    }

    //RecurrentTransaction
    private fun recurrentTransactionMap(): Map<TransactionRef, RecurrentTransaction> =
        recurrentTransactionLedgerContext.recurrentTransactionLedger.transactions.map { it.reference to it }
            .toMap()

    fun loadAllRecurrentTransactions(): List<RecurrentTransaction> =
        recurrentTransactionMap().values.toList()

    fun loadRecurrentTransactionUsingRefId(id: String): Option<RecurrentTransaction> =
        loadRecurrentTransactionUsingRef(TransactionRef(id))

    fun loadRecurrentTransactionUsingRef(ref: TransactionRef): Option<RecurrentTransaction> =
        recurrentTransactionMap().getOption(ref)

    fun saveRecurrentTransaction(recurrentTransaction: RecurrentTransaction) {
        recurrentTransactionLedgerContext = RecurrentTransactionLedgerContextAPI.run {
            recurrentTransactionLedgerContext.addRecurrentTransaction(recurrentTransaction)
        }
        fundViews =
            RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }
    }
}