package com.github.chenjianjx.srb4jfullsample.impl.itcase.bbs;

import com.github.chenjianjx.srb4jfullsample.impl.biz.bbs.Post;
import com.github.chenjianjx.srb4jfullsample.impl.biz.bbs.PostRepo;
import com.github.chenjianjx.srb4jfullsample.impl.itcase.BaseITCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * An exemplary repository itcase
 *
 * @author chenjianjx@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class PostRepoITCase extends BaseITCase {

    @Resource
    PostRepo repo;

    @Test
    public void crudTest() throws Exception {

        Post post = new Post();
        post.setContent(" It is now " + System.currentTimeMillis());
        post.setCreatedBy("tester");
        post.setUserId(1);

        // save
        repo.saveNewPost(post);
        long postId = post.getId();
        Assert.assertTrue(postId > 0);
        Assert.assertNotNull(post.getCreatedAt());

        // query by id
        post = repo.getPostById(postId);
        Assert.assertNotNull(post);
        System.out.println("the queried out post is " + post);

        // update
        post.setContent("updated content");
        repo.updatePost(post);
        post = repo.getPostById(postId);
        Assert.assertEquals("updated content", post.getContent());

        // delete
        repo.deletePostById(postId);
        Assert.assertNull(repo.getPostById(postId));

    }

}
