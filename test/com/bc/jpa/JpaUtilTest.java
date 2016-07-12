package com.bc.jpa;

import com.bc.jpa.query.QueryBuilder;
import com.bc.jpa.util.EntityMapBuilderImpl;
import com.bc.util.JsonBuilder;
import com.idisc.pu.entities.Archivedfeed;
import com.idisc.pu.entities.Bookmarkfeed;
import com.idisc.pu.entities.Feed;
import com.idisc.pu.entities.Site;
import com.idisc.pu.entities.Sitetype;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.TypedQuery;
import junit.framework.TestCase;

/**
 * @author Josh
 */
public class JpaUtilTest extends TestCase {
    
    public JpaUtilTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAll() throws IOException {
        
        System.out.println("#testAll");
        
        Feed feed = this.getDummyFeed(1, true);
        
        List ignoreList = Arrays.asList(Archivedfeed.class);
        
        Map appendTo = new EntityMapBuilderImpl(false, Integer.MAX_VALUE, 10, null, ignoreList).build(feed);
        
        JsonBuilder jb = new JsonBuilder(true);
        
        jb.appendJSONString(appendTo, System.out);
    }
    
    private Feed getDummyFeed(int id, boolean sitetypeToo) {
        
        Feed feed = new Feed();
        feed.setAuthor("Chinomso");
        Bookmarkfeed bm0 = this.getDummyBookmarkfeed(feed, 1);
        Bookmarkfeed bm1 = this.getDummyBookmarkfeed(feed, 2);
        feed.setBookmarkfeedList(Arrays.asList(bm0, bm1));
        feed.setCategories("Feed categories");
        feed.setCommentList(null);
        feed.setContent("Feed content");
        feed.setDatecreated(new Date());
        feed.setDescription("Feed description");
        feed.setExtradetails(null);
        feed.setFavoritefeedList(null);
        feed.setFeeddate(new Date());
        feed.setFeedhitList(null);
        feed.setFeedid(id);
        feed.setImageurl("Feed imageurl");
        feed.setKeywords("Feed keywords");
        feed.setRawid(null);
        Site site = this.getDummySite(id, sitetypeToo);
        feed.setSiteid(site);
        feed.setTimemodified(new Date());
        feed.setTitle("Feed title");
        feed.setUrl("Feed url");
        return feed;
    }

    private Site getDummySite(int id, boolean sitetypeToo) {
        Site site = new Site();
        site.setArchivedfeedList(null);
        site.setDatecreated(new Date());
        site.setExtradetails(null);
        site.setFeedList(null);
        site.setIconurl(null);
        site.setSite("Site sitename");
        site.setSiteid(id);
        if(sitetypeToo) {
            site.setSitetypeid(this.getDummySitetype(site, id));
        }
        site.setTimemodified(new Date());
        return site;
    }
    
    private Sitetype getDummySitetype(Site site, int id) {
        Sitetype st = new Sitetype();
        Site site_n = this.getDummySite(id + 2, false);
        st.setSiteList(Arrays.asList(site, this.getDummySite(id + 1, false), site_n));
        site_n.setFeedList(Arrays.asList(this.getDummyFeed(site_n.getSiteid(), false)));
        st.setSitetype("type: "+id);
        st.setSitetypeid((short)id);
        return st;
    }
    
    private Bookmarkfeed getDummyBookmarkfeed(Feed feed, int id) {
        Bookmarkfeed bm = new Bookmarkfeed();
        bm.setBookmarkfeedid(id);
        bm.setDatecreated(new Date());
        bm.setFeedid(feed);
        bm.setInstallationid(null);
        return bm;
    }
    
    private Feed getRemoteFeed() {
        
        JpaContext jpaContext = TestApp.getInstance().getIdiscJpaContext();
        
        QueryBuilder<Feed> qb = jpaContext.getQueryBuilder(Feed.class);
        
        TypedQuery<Feed> tq = qb.descOrder(Feed.class, "feedid").build();
        
        tq.setFirstResult(0).setMaxResults(20);
        
        List<Feed> found = tq.getResultList();
        
        Feed feed = found.get(found.size() - 1);
        
        return feed;
    }
}
