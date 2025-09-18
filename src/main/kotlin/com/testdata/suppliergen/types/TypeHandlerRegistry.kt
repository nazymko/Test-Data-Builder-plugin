package com.testdata.suppliergen.types

import com.testdata.suppliergen.types.contract.TypeHandler
import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.testdata.suppliergen.types.impl.*
import com.testdata.suppliergen.types.impl.collections.*
import com.testdata.suppliergen.types.impl.maps.*
import com.testdata.suppliergen.types.impl.patterns.*
import com.intellij.psi.PsiType

object TypeHandlerRegistry {
    // Pattern-based handlers - these should be checked first and sorted by priority
    private val patternBasedHandlers: List<PatternBasedTypeHandler> = listOf(
        // Crypto & Blockchain (Highest Priority)
        BitcoinAddressHandler,          // 26
        EthereumAddressHandler,         // 26
        WalletIdHandler,                // 25
        BlockchainHashHandler,          // 24

        // Financial Securities
        ISINTypeHandler,                // 25
        AskPriceTypeHandler,            // 24
        BidPriceTypeHandler,            // 24
        YieldTypeHandler,               // 23
        TickerSymbolTypeHandler,        // 22
        CUSIPTypeHandler,               // 21

        // Scientific & Research
        DOIHandler,                     // 20
        ResearchIdHandler,              // 19
        LabSampleHandler,               // 18
        ChemicalFormulaHandler,         // 17
        GenomeSequenceHandler,          // 16

        // Healthcare & Identity
        PatientIdTypeHandler,           // 20
        InsuranceTypeHandler,           // 19
        PassportTypeHandler,            // 18
        BloodTypeTypeHandler,           // 18
        DriverLicenseTypeHandler,       // 17

        // Legal & Compliance
        GDPRConsentHandler,             // 18
        ComplianceIdHandler,            // 17
        LegalDocumentHandler,           // 16
        CertificationHandler,           // 15

        // Media & Content
        ISBN13Handler,                  // 19
        MimeTypeHandler,                // 18
        FileExtensionHandler,           // 17
        ColorCodeHandler,               // 16
        MediaUrlHandler,                // 15

        // Gaming & Entertainment
        GameScoreHandler,               // 15
        PlayerIdHandler,                // 14
        StreamKeyHandler,               // 13
        GameAchievementHandler,         // 12
        GameSessionHandler,             // 11

        // IoT & Devices
        DeviceIdHandler,                // 15
        SensorValueHandler,             // 14
        BatteryLevelHandler,            // 13
        MachineLearningModelHandler,    // 12

        // Identity & Security
        CreditCardTypeHandler,          // 16
        NationalIdTypeHandler,          // 16
        SSNTypeHandler,                 // 15

        // Transportation & Logistics
        LicensePlateTypeHandler,        // 15
        PhoneTypeHandler,               // 14
        VinTypeHandler,                 // 14
        PersonalInfoTypeHandler,        // 13
        FlightNumberTypeHandler,        // 13
        TrackingNumberTypeHandler,      // 12
        IBANTypeHandler,                // 12

        // Business & Commerce
        GenderTypeHandler,              // 11
        SkuTypeHandler,                 // 11
        OrderNumberTypeHandler,         // 10
        EmailTypeHandler,               // 10
        JobTitleTypeHandler,            // 9
        SalaryTypeHandler,              // 9
        CustomerIdTypeHandler,          // 9

        // Professional & Geographic
        CompanyTypeHandler,             // 8
        CurrencyTypeHandler,            // 8
        ExpirationDateTypeHandler,      // 8
        DepartmentTypeHandler,          // 7
        NameTypeHandler,                // 7
        TimeZoneTypeHandler,            // 7

        // Technical & Geographic
        CoordinateTypeHandler,          // 6
        AddressTypeHandler,             // 6
        MacAddressTypeHandler,          // 6
        IPAddressTypeHandler,           // 5
        DomainTypeHandler,              // 5
        UuidTypeHandler                 // 4
    ).sortedByDescending { it.priority }

    private val handlers: List<TypeHandler> = listOf(
        StringHandler,
        BigDecimalHandler,
        BooleanHandler,
        ConcurrentHashMapHandler,
        EnumMapHandler,
        ListMultimapHandler,
        ConcurrentSkipListMapHandler,
        NavigableMapHandler,
        ConcurrentMapHandler,
        GuavaMapHandler,
        SynchronizedMapHandler,
        SortedMapHandler,
        HashMapHandler,
        CommonMapHandler(),
        IdentityHashMapHandler,
        LinkedHashMapHandler,
        TreeMapHandler,
        HashtableHandler,
        WeakHashMapHandler,
        BooleanWrapperHandler,
        DateHandler,
        DoubleHandler,
        DurationHandler,
        InstantHandler,
        IntHandler,
        LocalDateHandler,
        LocalDateTimeHandler,
        LongHandler,
        ObjectHandler,
        OffsetDateTimeHandler,
        OptionalHandler,
        URIHandler,
        URLHandler,
        UUIDHandler,
        ZoneIdHandler,
        EnumHandler,
        ArrayDequeHandler,
        BitSetHandler,
        BlockingQueueHandler,
        EnumSetHandler,
        LinkedBlockingDequeHandler,
        GuavaCollectionHandler,
        NavigableSetHandler,
        PriorityQueueHandler,
        PropertiesHandler,
        StackHandler,
        SynchronizedListHandler,
        SynchronizedSetHandler,
        TreeSetHandler,
        VectorHandler,
        LinkedBlockingQueueHandler,
        CollectionHandler,
    ) + CollectionHandlerRegistry.handlers

    // Cache helper instance
    private val cacheHelper = CacheHelper()

    /**
     * Resolve a type handler considering field name patterns for semantic data generation
     */
    fun resolve(fieldName: String?, fqName: String?, psiType: PsiType?, desc: String?): TypeHandler {
        // First check pattern-based handlers if we have a field name
        if (!fieldName.isNullOrBlank()) {
            val patternHandler = patternBasedHandlers.firstOrNull {
                it.supportsFieldName(fieldName, fqName, psiType)
            }
            if (patternHandler != null) {
                println("Field '$fieldName' matched pattern handler: $patternHandler")
                return patternHandler
            }
        }

        // Fall back to regular type-based resolution
        return resolve(fqName, psiType, desc)
    }

    fun resolve(fqName: String?, psiType: PsiType?, desc: String?): TypeHandler {
        val result: TypeHandler

        // Check cache first
        val cachedHandler = cacheHelper.getCachedHandler(fqName, psiType)

        if (cachedHandler != null) {
            println("fqName = [${fqName}], psiType = [${psiType}] resolved from CACHE = [${cachedHandler}]. Desc = [${desc}]")
            result = cachedHandler
        } else {
            // Use cache helper's recursion tracking
            result = cacheHelper.withRecursionTracking(
                fqName = fqName,
                psiType = psiType,
                onRecursion = {
                    println("RECURSION DETECTED for fqName = [${fqName}], psiType = [${psiType}]. Stack = [${cacheHelper.getCurrentResolutionStack()}]. Desc = [${desc}]")
                    // Cache and return UnknownPojoHandler to break the cycle
                    val recursiveHandler = UnknownPojoHandler
                    cacheHelper.cacheHandler(fqName, psiType, recursiveHandler)
                    recursiveHandler
                }
            ) {
                // Resolve handler
                val handler = handlers.firstOrNull { it.supports(fqName, psiType) } ?: UnknownPojoHandler
                println("fqName = [${fqName}], psiType = [${psiType}] resolved into handler = [${handler}]. Desc = [${desc}]")

                // Cache the result
                cacheHelper.cacheHandler(fqName, psiType, handler)
                handler
            }
        }

        if (result is UnknownPojoHandler) {
            println("Using UnknownPojoHandler for fqName = [${fqName}], psiType = [${psiType}]. Desc = [${desc}]")
        }
        return result
    }

    /**
     * Clear the handler cache - useful for testing or when types change
     */
    fun clearCache() {
        cacheHelper.clearCache()
        println("TypeHandlerRegistry cache cleared")
    }

    /**
     * Clear current thread's resolution stack - useful for cleanup
     */
    fun clearResolutionStack() {
        cacheHelper.clearResolutionStack()
    }

    /**
     * Get cache statistics for monitoring/debugging
     */
    fun getCacheStats(): CacheStats {
        return cacheHelper.getCacheStats()
    }

    /**
     * Check if a type is currently being resolved (for debugging)
     */
    fun isCurrentlyResolving(fqName: String?, psiType: PsiType?): Boolean {
        return cacheHelper.isCurrentlyResolving(fqName, psiType)
    }
}