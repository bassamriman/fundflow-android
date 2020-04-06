package com.rimanware.fundflow_android

import arrow.core.Option
import arrow.core.getOption
import fundflow.Fund
import fundflow.FundRef
import fundflow.ledgers.RecurrentTransactionFundView
import fundflow.ledgers.RecurrentTransactionLedgerContext
import fundflow.ledgers.RecurrentTransactionLedgerContextAPI

object DataManager {
    private var recurrentTransactionLedgerContext = RecurrentTransactionLedgerContext.empty()
    private var fundViews: Map<FundRef, RecurrentTransactionFundView> =
        RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }

    fun fundMap(): Map<FundRef, Fund> = recurrentTransactionLedgerContext.funds
    fun funds(): List<Fund> = fundMap().values.toList()
    fun getFundByRefId(id: String): Option<Fund> = getFundByRef(FundRef(id))
    fun getFundByRef(ref: FundRef): Option<Fund> = fundMap().getOption(ref)
    fun addFund(fund: Fund) {
        recurrentTransactionLedgerContext = RecurrentTransactionLedgerContextAPI.run {
            recurrentTransactionLedgerContext.addFund(fund)
        }
        fundViews = RecurrentTransactionLedgerContextAPI.run { recurrentTransactionLedgerContext.viewAll() }
    }


}