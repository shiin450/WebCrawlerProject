package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CrawlTask extends RecursiveAction {


    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    private final String url;
    private final Instant deadline;
    private final int maxDepth;
    private final Map<String, Integer> counts;
    private final ConcurrentSkipListSet<String> visitedUrls;

    public CrawlTask(Clock clock,
                     PageParserFactory parserFactory,
                     List<Pattern> ignoredUrls,
                     String url,
                     Instant deadline,
                     int maxDepth,
                     Map<String, Integer> counts,
                     ConcurrentSkipListSet<String> visitedUrls) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.ignoredUrls = ignoredUrls;
        this.url = url;
        this.deadline = deadline;
        this.maxDepth = maxDepth;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
    }


    @Override
    protected void compute() {
        //check conditions
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }
        if (visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        PageParser.Result result = parserFactory.get(url).parse();
        popularWordsCounter(result, counts);

        List<String> sublinks = result.getLinks();
        List<CrawlTask> subtasks =
                sublinks.stream().map(sublink -> new CrawlTaskBuilder()
                                .setClock(clock)
                                .setCounts(counts)
                                .setDeadline(deadline)
                                .setIgnoredUrls(ignoredUrls)
                                .setMaxDepth(maxDepth - 1)
                                .setParserFactory(parserFactory)
                                .setUrl(sublink)
                                .setVisitedUrls(visitedUrls)
                                .createCrawlTask())
                        .collect(Collectors.toList());

        invokeAll(subtasks);


    }

    public static void popularWordsCounter(PageParser.Result result, Map<String, Integer> counts) {
        for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : counts.get(e.getKey()) + e.getValue());
        }
    }


}
