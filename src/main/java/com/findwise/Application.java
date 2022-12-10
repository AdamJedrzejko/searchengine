package com.findwise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

@RestController
class Controller implements SearchEngine {
	private final List<Document> documentList = new ArrayList<>();
	private final Map<String, List<Entry>> index = new HashMap<>();

	@PostMapping("/documents")
	public void addDocument(@RequestBody List<Document> documentList) {
		this.documentList.addAll(documentList);
		for (Document document : documentList) {
			indexDocument(document.getId(), document.getContent());
		}
	}

	@Override
	public void indexDocument(String id, String content) {
		for (String word : content.split(" ")) {
			word = word.toLowerCase();
			List<Entry> entryList = index.get(word);
			Entry entry = new Entry();
			entry.setId(id);
			entry.setScore(tf(new Document(id, content), word) * idf(word));
			if (entryList == null) {
				entryList = new ArrayList<>();
				index.put(word, entryList);
			}
			boolean isUpdate = true;
			for (Entry entryCheck : entryList) {
				if (entryCheck.getId().equals(entry.getId())) {
					isUpdate = false;
					break;
				}
			}
			if (isUpdate) {
				entryList.add(entry);
			}
		}
	}

	@Override
	@GetMapping("/search/{term}")
	public List<Entry> search(@PathVariable String term) {
		List<Entry> entryList = index.get(term);
		if (entryList == null) {
			return new ArrayList<>();
		}
		Collections.sort(entryList);
		return entryList;
	}

	public double tf(Document document, String term) {
		double result = 0;
		for (String word : document.getContent().split(" ")) {
			if (term.equalsIgnoreCase(word))
				result++;
		}
		return result / document.getContent().split(" ").length;
	}

	public double idf(String term) {
		double n = 0;
		for (Document document : documentList) {
			for (String word : document.getContent().split(" ")) {
				if (term.equalsIgnoreCase(word)) {
					n++;
					break;
				}
			}
		}
		return Math.log(documentList.size() / n);
	}
}

class Entry implements IndexEntry, Comparable<Entry> {
	private String id;
	private double score;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(Entry o) {
		if (this.score - o.score == 0) {
			return 0;
		} else if (this.score - o.score < 0) {
			return 1;
		} else {
			return -1;
		}
	}
}

interface IndexEntry {
	String getId();
	void setId(String id);
	double getScore();
	void setScore(double score);
}

interface SearchEngine {
	/**
	 * Add a document to the index
	 *
	 * @param id name of the indexed document
	 * @param content content of the document
	 */
	void indexDocument(String id, String content);
	/**
	 * Search the index for the given term
	 *
	 * @param term to be found
	 * @return sorted list of search results containing the given term
	 */
	List<Entry> search(String term);
}

@Getter
@AllArgsConstructor
class Document {
	private String id;
	private String content;
}