package io.velocitycareerlabs.entities

import io.velocitycareerlabs.api.entities.VCLFilter
import io.velocitycareerlabs.api.entities.VCLOrganizationsSearchDescriptor
import io.velocitycareerlabs.infrastructure.resources.valid.OrganizationsDescriptorMocks
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Michael Avoyan on 8/15/21.
 */
internal class VCLOrganizationsSearchDescriptorTest {

    lateinit var subject: VCLOrganizationsSearchDescriptor

    @Before
    fun setUp() {
    }

    @Test
    fun testOrganizationsDescriptorAllParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "filter.did=did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88&" +
                    "filter.serviceTypes=Inspector&" +
                    "filter.credentialTypes=EducationDegree&" +
                    "sort[0]=createdAt,DESC&sort[1]=pdatedAt,ASC&" +
                    "page.skip=1&" +
                    "page.size=1&q=Bank"
        subject = VCLOrganizationsSearchDescriptor(
            filter = OrganizationsDescriptorMocks.Filter,
            page = OrganizationsDescriptorMocks.Page,
            sort = OrganizationsDescriptorMocks.Sort,
            query = OrganizationsDescriptorMocks.Query
        )

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorFilterPageSortParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "filter.did=did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88&" +
                    "filter.serviceTypes=Inspector&" +
                    "filter.credentialTypes=EducationDegree&" +
                    "sort[0]=createdAt,DESC&sort[1]=pdatedAt,ASC&" +
                    "page.skip=1&page.size=1"
        subject = VCLOrganizationsSearchDescriptor(
            filter = OrganizationsDescriptorMocks.Filter,
            page = OrganizationsDescriptorMocks.Page,
            sort = OrganizationsDescriptorMocks.Sort
        )

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorFilterPageQueryParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "filter.did=did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88&" +
                    "filter.serviceTypes=Inspector&" +
                    "filter.credentialTypes=EducationDegree&" +
                    "page.skip=1&" +
                    "page.size=1&" +
                    "q=Bank"
        subject = VCLOrganizationsSearchDescriptor(
            filter = OrganizationsDescriptorMocks.Filter,
            page = OrganizationsDescriptorMocks.Page,
            query = OrganizationsDescriptorMocks.Query
        )

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorFilterSortQueryParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "filter.did=did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88&" +
                    "filter.serviceTypes=Inspector&" +
                    "filter.credentialTypes=EducationDegree&" +
                    "sort[0]=createdAt,DESC&" +
                    "sort[1]=pdatedAt,ASC&" +
                    "q=Bank"
        subject = VCLOrganizationsSearchDescriptor(
            filter = OrganizationsDescriptorMocks.Filter,
            sort = OrganizationsDescriptorMocks.Sort,
            query = OrganizationsDescriptorMocks.Query
        )

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorPageSortQueryParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "sort[0]=createdAt,DESC&" +
                    "sort[1]=pdatedAt,ASC&" +
                    "page.skip=1&" +
                    "page.size=1&" +
                    "q=Bank"
        subject = VCLOrganizationsSearchDescriptor(
            page = OrganizationsDescriptorMocks.Page,
            sort = OrganizationsDescriptorMocks.Sort,
            query = OrganizationsDescriptorMocks.Query
        )

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorDidFilterAggregationSuccess() {
        val organizationDescriptorQueryParamsMock =
            "filter.did=did:velocity:0x2bef092530ccc122f5fe439b78eddf6010685e88"
        subject =
            VCLOrganizationsSearchDescriptor(filter = VCLFilter(did = OrganizationsDescriptorMocks.Filter.did))

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @Test
    fun testOrganizationsDescriptorNoParamsAggregationSuccess() {
        val organizationDescriptorQueryParamsMock = null
        subject = VCLOrganizationsSearchDescriptor()

        assert(subject.queryParams == organizationDescriptorQueryParamsMock)
    }

    @After
    fun tearDown() {
    }
}