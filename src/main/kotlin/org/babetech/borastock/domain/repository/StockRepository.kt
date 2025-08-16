package org.babetech.borastock.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.babetech.borastock.domain.models.*

interface StockRepository {
    // Stock Items
    val stockItems: StateFlow<List<StockItem>>
    suspend fun addStockItem(item: StockItem)
    suspend fun updateStockItem(item: StockItem)
    suspend fun deleteStockItem(itemId: String)
    
    // Stock Entries
    val stockEntries: StateFlow<List<StockEntry>>
    suspend fun addStockEntry(entry: StockEntry)
    suspend fun updateStockEntry(entry: StockEntry)
    suspend fun deleteStockEntry(entryId: String)
    
    // Stock Exits
    val stockExits: StateFlow<List<StockExit>>
    suspend fun addStockExit(exit: StockExit)
    suspend fun updateStockExit(exit: StockExit)
    suspend fun deleteStockExit(exitId: String)
    
    // Suppliers
    val suppliers: StateFlow<List<Supplier>>
    suspend fun addSupplier(supplier: Supplier)
    suspend fun updateSupplier(supplier: Supplier)
    suspend fun deleteSupplier(supplierId: String)
    
    // Statistics
    fun getStockStatistics(): List<StockStat>
    fun getEntryStatistics(): List<StockStat>
    fun getExitStatistics(): List<StockStat>
}

data class StockStat(
    val title: String,
    val value: String,
    val iconRes: DrawableResource,
    val color: Color
)

data class StockSummary(
    val label: String,
    val value: String,
    val iconRes: DrawableResource,
    val iconTint: Color,
    val backgroundColor: Color,
    val valueColor: Color
)