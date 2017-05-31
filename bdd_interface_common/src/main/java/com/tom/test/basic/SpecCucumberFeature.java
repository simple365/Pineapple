package com.tom.test.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cucumber.runtime.FeatureBuilder;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.PathWithLines;
import gherkin.formatter.model.Feature;

public class SpecCucumberFeature extends CucumberFeature {
	private String absolutePath;
	public SpecCucumberFeature(Feature feature, String path) {
		super(feature, path);
		// TODO Auto-generated constructor stub
	}

	public static List<CucumberFeature> load(ResourceLoader resourceLoader, List<String> featurePaths, final List<Object> filters) {
		final List<CucumberFeature> cucumberFeatures = new ArrayList<CucumberFeature>();
		final SpecFeatureBuilder builder = new SpecFeatureBuilder(cucumberFeatures);
		for (String featurePath : featurePaths) {
			if (featurePath.startsWith("@")) {
				loadFromRerunFile(builder, resourceLoader, featurePath.substring(1), filters);
			} else {
				loadFromFeaturePath(builder, resourceLoader, featurePath, filters, false);
			}
		}
		Collections.sort(cucumberFeatures, new CucumberFeatureUriComparator());
		return cucumberFeatures;
	}

	private static void loadFromRerunFile(SpecFeatureBuilder builder, ResourceLoader resourceLoader, String rerunPath, final List<Object> filters) {
		Iterable<Resource> resources = resourceLoader.resources(rerunPath, null);
		for (Resource resource : resources) {
			String source = builder.read(resource);
			if (!source.isEmpty()) {
				for (String featurePath : source.split(" ")) {
					loadFromFileSystemOrClasspath(builder, resourceLoader, featurePath, filters);
				}
			}
		}
	}

	private static void loadFromFileSystemOrClasspath(SpecFeatureBuilder builder, ResourceLoader resourceLoader, String featurePath, final List<Object> filters) {
		try {
			loadFromFeaturePath(builder, resourceLoader, featurePath, filters, false);
		} catch (IllegalArgumentException originalException) {
			if (!featurePath.startsWith(MultiLoader.CLASSPATH_SCHEME) && originalException.getMessage().contains("Not a file or directory")) {
				try {
					loadFromFeaturePath(builder, resourceLoader, MultiLoader.CLASSPATH_SCHEME + featurePath, filters, true);
				} catch (IllegalArgumentException secondException) {
					if (secondException.getMessage().contains("No resource found for")) {
						throw new IllegalArgumentException("Neither found on file system or on classpath: " + originalException.getMessage() + ", " + secondException.getMessage());
					} else {
						throw secondException;
					}
				}
			} else {
				throw originalException;
			}
		}
	}

	private static void loadFromFeaturePath(SpecFeatureBuilder builder, ResourceLoader resourceLoader, String featurePath, final List<Object> filters, boolean failOnNoResource) {
		PathWithLines pathWithLines = new PathWithLines(featurePath);
		ArrayList<Object> filtersForPath = new ArrayList<Object>(filters);
		filtersForPath.addAll(pathWithLines.lines);
		Iterable<Resource> resources = resourceLoader.resources(pathWithLines.path, ".feature");
		if (failOnNoResource && !resources.iterator().hasNext()) {
			throw new IllegalArgumentException("No resource found for: " + pathWithLines.path);
		}
		for (Resource resource : resources) {
			builder.parse(resource, filtersForPath);
		}
	}

	private static class CucumberFeatureUriComparator implements Comparator<CucumberFeature> {
		@Override
		public int compare(CucumberFeature a, CucumberFeature b) {
			return a.getPath().compareTo(b.getPath());
		}
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

}
