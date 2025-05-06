package com.example.ExampleApiDemo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.ExampleApiDemo.model.Credit;
import com.example.ExampleApiDemo.model.ProjectDetail;
import com.example.ExampleApiDemo.model.ProjectExperience;
import com.example.ExampleApiDemo.model.ResumeData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DownloadService {

	public byte[] downloadResume(ResumeData resumeData) {
		try (XWPFDocument document = new XWPFDocument(new ClassPathResource("Maveric_Template.docx").getInputStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			// Process headers
			processHeaders(document.getHeaderList(), resumeData.getHeaders().getCandidateName(),
					resumeData.getHeaders().getCandidatePosition());

			// Process summary
			processSummary(document, "SUMMARY", resumeData.getProfessionalSummary());

			// Process bullet list
			processBulletList(document, "EXPERIENCE", resumeData.getProfessionalExperience());

			// Process bullet list
			processBulletList(document, "AWARDS", resumeData.getAwards());

			// Process certifications
			processBulletList(document, "CERTIFICATION", resumeData.getCertifications());

			// Process educations
			processBulletList(document, "EDUCATION", resumeData.getEducation());

			// Process credits
			processCreditTable(document, "CREDITS", resumeData.getCredits());

			// Process project experience
			processProjectExperienceTable(document, "PROJECT_EXPERIENCE",
					resumeData.getProjectExperience());

			// Remove blank sections
			removeBlankSections(document, resumeData);

			document.write(out);
			return out.toByteArray();

		} catch (IOException e) {
			log.error("Error while creating file: ", e);
		}
		return new byte[0];
	}

	private void removeBlankSections(XWPFDocument doc, ResumeData resumeData) {

		if (resumeData.getAwards() == null || (resumeData.getAwards() != null && resumeData.getAwards().isEmpty())) {
			removeSection("Awards & Recognitions", doc);
		}
		if (resumeData.getCertifications() == null
				|| (resumeData.getCertifications() != null && resumeData.getCertifications().isEmpty())) {
			removeSection("Certifications and Courses", doc);
		}
		if (resumeData.getEducation() == null
				|| (resumeData.getEducation() != null && resumeData.getEducation().isEmpty())) {
			removeSection("Educational Qualification", doc);
		}
		if (resumeData.getCredits() == null
				|| (resumeData.getCredits() != null && resumeData.getCredits().isEmpty())) {
			removeSection("Credits", doc);
		}
		if (resumeData.getProjectExperience() == null
				|| (resumeData.getProjectExperience() != null && resumeData.getProjectExperience().isEmpty())) {
			removeSection("Project Experience", doc);
		}

	}

	private void removeSection(String sectionName, XWPFDocument doc) {
		List<IBodyElement> bodyElements = doc.getBodyElements();

		for (int i = 0; i < bodyElements.size(); i++) {
			IBodyElement element = bodyElements.get(i);

			if (element.getElementType() == BodyElementType.PARAGRAPH) {
				XWPFParagraph paragraph = (XWPFParagraph) element;
				String text = paragraph.getText().trim();

				if (sectionName.equalsIgnoreCase(text)) {
					// Check for shading
					CTPPr pPr = paragraph.getCTP().getPPr();
					if (pPr != null && pPr.isSetShd()) {
						// Shading is present, remove the entire paragraph
						doc.removeBodyElement(i);
						i--; // Adjust index after removal
					}
				}
			}
		}
	}

	private void processProjectExperienceTable(XWPFDocument doc, String placeholder,
			List<ProjectExperience> projectExperience) {

		List<XWPFParagraph> paragraphs = doc.getParagraphs();

		for (int i = 0; i < paragraphs.size(); i++) {
			XWPFParagraph paragraph = paragraphs.get(i);
			String text = paragraph.getText();

			if (text.contains(placeholder)) {
				// Remove runs containing the placeholder text
				for (XWPFRun run : paragraph.getRuns()) {
					if (run.getText(0) != null && run.getText(0).contains(placeholder)) {
						run.setText("", 0); // Clear the placeholder text
					}
				}

				// Insert table right after the paragraph
				XmlCursor cursor = paragraph.getCTP().newCursor();
				XWPFTable table = doc.insertNewTbl(cursor);

				// Add data rows
				for (ProjectExperience experience : projectExperience) {
					XWPFTableRow row = table.createRow();

					// Ensure all cells are created before setting text
					XWPFTableCell cell0 = row.getCell(0);
					if (cell0 == null) {
						cell0 = row.createCell();
					}
					// Clear existing paragraphs (optional but clean)
					cell0.removeParagraph(0);
					// Create a new paragraph and run
					XWPFParagraph para = cell0.addParagraph();

					for (ProjectDetail item : experience.getProjectDetails()) {
						if (item.getValue() instanceof String) {
							createCellOfClientColumn(item.getValue().toString(), item.getKey() + ": ", para);
						} else if (item.getValue() instanceof List<?>) {
							createCellOfClientColumn(
									String.join(", ",
											Objects.requireNonNullElse(
													Arrays.asList(item.getValue().toString().split(",")),
													Collections.emptyList())),
									item.getKey() + ": ",
									para);
						}

					}

					// Set width for first column
					CTTcPr tcPr = cell0.getCTTc().addNewTcPr();
					CTTblWidth width = tcPr.addNewTcW();
					width.setW(BigInteger.valueOf(3000)); // width in TWIPS (1/20th of a point)
					width.setType(STTblWidth.DXA);

					cell0.getCTTc().addNewTcPr().addNewShd().setFill("d8d4d4");

					// Ensure all cells are created before setting text
					XWPFTableCell cell1 = row.getCell(1);

					if (cell1 == null) {
						cell1 = row.createCell();
					}

					// Set width for 2nd column
					CTTcPr tcPr2 = cell1.getCTTc().addNewTcPr();
					CTTblWidth width2 = tcPr2.addNewTcW();
					width2.setW(BigInteger.valueOf(6000)); // width in TWIPS (1/20th of a point)
					width2.setType(STTblWidth.DXA);

					// Clear existing paragraphs (optional but clean)
					cell1.removeParagraph(0);
					// Create a new paragraph and run
					para = cell1.addParagraph();

					createDescriptionCell(experience.getDescription(), "Description: ", para);
					createResponsibilitiesCell(experience.getResponsibilities(),
							"Responsibilities: ", doc, cell1);

				}
				if (table.getNumberOfRows() > 0) {
					table.removeRow(0);
				}
				break;
			}
		}
	}

	private void createCellOfClientColumn(String value, String label, XWPFParagraph para) {
		if (StringUtils.hasText(value)) {
			XWPFRun labelRun = para.createRun();
			labelRun.setBold(true);
			labelRun.setText(label);
			XWPFRun valueRun = para.createRun();
			valueRun.setBold(false);
			valueRun.setText(value);
			valueRun.addBreak();
		}
	}

	private void createDescriptionCell(String value, String label, XWPFParagraph para) {
		if (StringUtils.hasText(value)) {
			XWPFRun labelRun = para.createRun();
			labelRun.setBold(true);
			labelRun.setText(label);
			labelRun.addBreak();

			XWPFRun valueRun = para.createRun();
			valueRun.setBold(false);
			valueRun.setText(value);
			valueRun.addBreak();
			valueRun.addBreak();
		}
	}

	private void createResponsibilitiesCell(List<String> values, String label, XWPFDocument doc, XWPFTableCell cell) {
		if (values != null && !values.isEmpty()) {
			// Get the first paragraph (default) inside the cell
			XWPFParagraph labelPara = cell.getParagraphs().get(0);
			XWPFRun labelRun = labelPara.createRun();
			labelRun.setBold(true);
			labelRun.setText(label);

			labelPara.setSpacingAfter(0);

			// Create numbering if not exists
			XWPFNumbering numbering = doc.getNumbering();
			if (numbering == null) {
				numbering = doc.createNumbering();
			}

			// Create bullet list style if not exists
			BigInteger numId = getOrCreateBulletNumbering(doc, numbering);

			// Add bullets
			for (String value : values) {
				XWPFParagraph bulletPara = cell.addParagraph();
				bulletPara.setNumID(numId); // ✨ IMPORTANT: set bullet numbering

				// Set paragraph spacing for cleaner look (optional)
				bulletPara.setSpacingBefore(0);
				bulletPara.setSpacingAfter(0);
				bulletPara.setSpacingBetween(1.0, LineSpacingRule.AUTO);

				XWPFRun bulletRun = bulletPara.createRun();
				bulletRun.setText(value);
			}
		}
	}

	private void processCreditTable(XWPFDocument doc, String placeholder, List<Credit> credits) {
		List<IBodyElement> bodyElements = new ArrayList<>(doc.getBodyElements());

		for (int i = 0; i < bodyElements.size(); i++) {
			IBodyElement element = bodyElements.get(i);

			if (element instanceof XWPFParagraph) {
				XWPFParagraph paragraph = (XWPFParagraph) element;
				String text = paragraph.getText();

				if (text != null && text.contains(placeholder)) {
					int pos = doc.getPosOfParagraph(paragraph);
					doc.removeBodyElement(pos);

					// Create the table and insert at position
					XmlCursor cursor = doc.getParagraphArray(pos).getCTP().newCursor();
					XWPFTable table = doc.insertNewTbl(cursor);

					// Add rows for each Credit object

					for (Credit credit : credits) {
						if (StringUtils.hasText(credit.getCategory()) && !credit.getItems().isEmpty()) {
							XWPFTableRow row = table.createRow();
							XWPFTableCell cell1 = row.getCell(0);

							// Set width for first column
							CTTcPr tcPr = cell1.getCTTc().addNewTcPr();
							CTTblWidth width = tcPr.addNewTcW();
							width.setW(BigInteger.valueOf(3000)); // width in TWIPS (1/20th of a point)
							width.setType(STTblWidth.DXA);

							// Set the background color of the first column to red
							cell1.getCTTc().addNewTcPr().addNewShd().setFill("d8d4d4");

							// Make the text in the first column bold
							XWPFParagraph paragraph1 = cell1.getParagraphArray(0);
							XWPFRun run = paragraph1.createRun();
							run.setBold(true); // Set text to bold
							run.setText(credit.getCategory());

							// Add the second column with the items
							XWPFTableCell cell2 = row.addNewTableCell();

							// Set width for first column
							CTTcPr tcPr2 = cell2.getCTTc().addNewTcPr();
							CTTblWidth width2 = tcPr2.addNewTcW();
							width2.setW(BigInteger.valueOf(6000)); // width in TWIPS (1/20th of a point)
							width2.setType(STTblWidth.DXA);

							cell2.setText(String.join(", ", credit.getItems()));
						}

					}
					// Remove the first row after the table is populated
					table.removeRow(0);
					break; // done after first match
				}
			}
		}
	}

	private void processHeaders(List<XWPFHeader> headerList, String name, String position) {
		// Replace text in headers
		for (XWPFHeader header : headerList) {
			for (XWPFTable table : header.getTables()) {
				for (XWPFTableRow row : table.getRows()) {
					List<XWPFTableCell> cells = row.getTableCells();
					if (cells.size() >= 2) {
						replaceTextInCell(cells.get(0), "NAME", name);
						replaceTextInCell(cells.get(1), "POSITION", position);
					}
				}
			}
		}
	}

	private void processSummary(XWPFDocument document, String placeholder, String summary) {
		// Replace in body paragraphs
		for (XWPFParagraph paragraph : document.getParagraphs()) {
			replaceInParagraph(paragraph, placeholder, summary);
		}

		// Replace in body tables
		for (XWPFTable table : document.getTables()) {
			for (XWPFTableRow row : table.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph paragraph : cell.getParagraphs()) {
						replaceInParagraph(paragraph, placeholder, summary);
					}
				}
			}
		}
	}

	private void processBulletList(XWPFDocument doc, String placeholder, List<String> bulletPoints) {
		List<XWPFParagraph> paragraphs = doc.getParagraphs();

		// Create numbering if needed
		XWPFNumbering numbering = doc.getNumbering();
		if (numbering == null) {
			numbering = doc.createNumbering();
		}

		// Check if bullet AbstractNum already exists
		BigInteger numId = getOrCreateBulletNumbering(doc, numbering);

		for (int i = 0; i < paragraphs.size(); i++) {
			XWPFParagraph para = paragraphs.get(i);
			String text = para.getText();

			if (text != null && text.contains(placeholder)) {
				int pos = doc.getPosOfParagraph(para);

				// Remove the placeholder paragraph
				doc.removeBodyElement(pos);

				// Insert bullet paragraphs
				for (int j = 0; j < bulletPoints.size(); j++) {
					XmlCursor cursor;
					if (pos + j < doc.getParagraphs().size()) {
						cursor = doc.getParagraphArray(pos + j).getCTP().newCursor();
					} else {
						// fallback if cursor cannot be found (at end of document)
						cursor = doc.getDocument().getBody().newCursor();
					}

					XWPFParagraph newPara = doc.insertNewParagraph(cursor);
					newPara.setNumID(numId);

					// Set paragraph spacing
					newPara.setSpacingBefore(0);
					newPara.setSpacingAfter(0);
					newPara.setSpacingBetween(1.0, LineSpacingRule.AUTO);

					XWPFRun run = newPara.createRun();
					run.setText(bulletPoints.get(j));
				}

				break; // Done processing
			}
		}
	}

	private BigInteger getOrCreateBulletNumbering(XWPFDocument doc, XWPFNumbering numbering) {
		// Try to reuse existing numbering if available
		for (XWPFAbstractNum absNum : numbering.getAbstractNums()) {
			CTAbstractNum ctAbsNum = absNum.getCTAbstractNum();
			if (ctAbsNum.getLvlArray(0).getNumFmt().getVal() == STNumberFormat.BULLET) {
				BigInteger abstractNumId = ctAbsNum.getAbstractNumId();
				return numbering.addNum(abstractNumId);
			}
		}

		// Create new bullet numbering
		CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();
		abstractNum.setAbstractNumId(BigInteger.valueOf(numbering.getAbstractNums().size())); // unique id

		CTLvl lvl = abstractNum.addNewLvl();
		lvl.setIlvl(BigInteger.ZERO);

		CTNumFmt numFmt = lvl.addNewNumFmt();
		numFmt.setVal(STNumberFormat.BULLET);

		CTLevelText lvlText = lvl.addNewLvlText();
		lvlText.setVal("•"); // bullet symbol

		lvl.addNewLvlJc().setVal(STJc.LEFT);

		CTInd ind = lvl.addNewPPr().addNewInd();
		ind.setLeft(BigInteger.valueOf(720)); // indent
		ind.setHanging(BigInteger.valueOf(360)); // hanging indent

		XWPFAbstractNum bulletAbstractNum = new XWPFAbstractNum(abstractNum);

		BigInteger abstractNumID = numbering.addAbstractNum(bulletAbstractNum);
		return numbering.addNum(abstractNumID);
	}

	private void replaceInParagraph(XWPFParagraph paragraph, String placeholder, String summary) {
		if (StringUtils.hasText(summary)) {
			for (XWPFRun run : paragraph.getRuns()) {
				String text = run.getText(0);
				if (text != null) {
					if (text.contains(placeholder)) {
						text = text.replace(placeholder, summary);
					}
					run.setText(text, 0);
				}
			}
		}
	}

	private void replaceTextInCell(XWPFTableCell cell, String placeholder, String value) {
		if (StringUtils.hasText(value)) {
			for (XWPFParagraph paragraph : cell.getParagraphs()) {
				for (XWPFRun run : paragraph.getRuns()) {
					String text = run.getText(0);
					if (text != null && text.contains(placeholder)) {
						run.setText(text.replace(placeholder, value), 0);
					}
				}
			}
		}
	}

}
