package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

public class CrawlTaskBuilder {
    private Clock clock;
    private PageParserFactory parserFactory;
    private List<Pattern> ignoredUrls;
    private String url;
    private Instant deadline;
    private int maxDepth;
    private Map<String, Integer> counts;
    private ConcurrentSkipListSet<String> visitedUrls;

    public CrawlTaskBuilder setClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    public CrawlTaskBuilder setParserFactory(PageParserFactory parserFactory) {
        this.parserFactory = parserFactory;
        return this;
    }

    public CrawlTaskBuilder setIgnoredUrls(List<Pattern> ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
        return this;
    }

    public CrawlTaskBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public CrawlTaskBuilder setDeadline(Instant deadline) {
        this.deadline = deadline;
        return this;
    }

    public CrawlTaskBuilder setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public CrawlTaskBuilder setCounts(Map<String, Integer> counts) {
        this.counts = counts;
        return this;
    }

    public CrawlTaskBuilder setVisitedUrls(ConcurrentSkipListSet<String> visitedUrls) {
        this.visitedUrls = visitedUrls;
        return this;
    }

    public CrawlTask createCrawlTask() {
        return new CrawlTask(clock, parserFactory, ignoredUrls, url, deadline, maxDepth, counts, visitedUrls);
    }
}