package com.exadel.aem.it.tests;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.assertion.CQAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.testing.junit.rules.Page;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Test that a page can be successfully created on the author instance. This
 * test showcases some <a
 * href="https://github.com/adobe/aem-testing-clients/wiki/Best-practices">best
 * practices</a> of the <a
 * href="https://github.com/adobe/aem-testing-clients">AEM Testing Clients</a>.
 */
public class CreatePageIT {

    private static final long TIMEOUT = MINUTES.toMillis(3);

    // The CQAuthorClassRule represents an author service. The rule will read
    // the hostname and port of the author service from the system properties
    // passed to the tests.

    @ClassRule
    public static final CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();

    // CQRule decorates your test and adds additional functionality on top of
    // it, like session stickyness, test filtering and identification of the
    // test on the remote service.

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule);

    // Page will create a test page with a random name and it will make sure
    // that the page is removed at the end of every test execution. By using a
    // random name, your test will not conflict with any other test running on
    // the same instance. By removing the page at the end of the test execution,
    // you are not going to leave any clutter on the instance under test.

    @Rule
    public Page root = new Page(cqBaseClassRule.authorRule);

    static CQClient adminAuthor;

    // Thanks to the CQAuthorClassRule, we can create a CQClient bound to the
    // admin user on the author instance.

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testCreatePage() throws InterruptedException {

        // Assert that the page (created with the Page rule above) exists on the
        // admin instance. Under the hood, this assertion implements a
        // retry-loop with a timeout. The retry-loop prevents your test from
        // failing due to the eventual consistency model of the persistence
        // layer, and the timeout from hanging forever in case an error occurs
        // and a page can't be created in time.

        CQAssert.assertCQPageExistsWithTimeout(adminAuthor, root.getPath(), TIMEOUT, 500);
    }

}
