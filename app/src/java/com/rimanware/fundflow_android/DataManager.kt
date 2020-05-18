package com.rimanware.fundflow_android

import arrow.core.Option
import arrow.core.getOption
import common.unit.TimeFrequency
import fundflow.Fund
import fundflow.FundRef
import fundflow.ledgers.CombinableRecurrentTransactionFundView
import fundflow.ledgers.CombinableRecurrentTransactionFundViewFactory
import fundflow.ledgers.RecurrentTransaction
import fundflow.ledgers.RecurrentTransactionFundView
import fundflow.ledgers.RecurrentTransactionLedgerContext
import fundflow.ledgers.RecurrentTransactionLedgerContextAPI
import java.time.LocalDateTime
import ledger.LedgerContextAPI
import ledger.TransactionRef

object DataManager {
    var recurrentTransactionLedgerContext = RecurrentTransactionLedgerContext.empty()
    var fundViews: Map<FundRef, RecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }

    // Fund
    private fun fundMap(): Map<FundRef, Fund> = recurrentTransactionLedgerContext.funds
    fun loadAllFunds(): List<Fund> = fundMap().values.toList()

    fun loadFundUsingRefId(id: String): Option<Fund> = loadFundUsingRef(FundRef(id))
    fun loadFundUsingRef(ref: FundRef): Option<Fund> = fundMap().getOption(ref)
    fun loadFundView(ref: FundRef): Option<RecurrentTransactionFundView> = fundViews.getOption(ref)

    fun loadFundFlowView(
        ref: FundRef,
        dateTime: LocalDateTime,
        timeFrequency: TimeFrequency
    ): Option<CombinableRecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run {
            LedgerContextAPI.run {
                val a = recurrentTransactionLedgerContext.flowAt(dateTime, timeFrequency)
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

    // RecurrentTransaction
    private fun recurrentTransactionMap(): Map<TransactionRef, RecurrentTransaction> =
        recurrentTransactionLedgerContext.recurrentTransactionLedger.transactions

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
