#!/bin/bash

REPORT_DIR='Report/out/2022-2023-FILA3-Capitrain-BARDON-Remi-ROURET-Lucas'
REPORT_BUILD_DIR='Report/out/REPORT'
REPORT_BUILD_FILE="${REPORT_BUILD_DIR}/README.md"
REPORT_PDF="${REPORT_BUILD_FILE%.md}.pdf"
REPORT_PARTS=( 'Report/ORGANISATION.md' 'Report/DONE.md' 'Report/STRENGTHS.md' 'Report/WEAKNESSES.md' 'Report/CHOICE.md' 'Report/WHAT-WE-WOULD-HAVE-DONE-DIFFERENTLY.md' 'Report/OTHER-INFOS.md' 'Report/LOG.md' )
UML_BUILD_DIR='Report/out/UML'
UML_BUILD_FILE="${UML_BUILD_DIR}/UML.md"
UML_PDF="${UML_BUILD_FILE%.md}.pdf"
UML_FILE='UML.plantuml'

# Cleanup old generated files
rm -rf "${REPORT_DIR}"
rm -rf "${REPORT_BUILD_FILE}"

# Create destination directory
mkdir -p "${REPORT_DIR}"

# Create report build file
mkdir -p "${REPORT_BUILD_DIR}"
cat 'Report/HEADER.md' > "${REPORT_BUILD_FILE}"

# Concatenate report parts
for f in "${REPORT_PARTS[@]}"; do
	echo '' >> "${REPORT_BUILD_FILE}"
	cat "${f}" >> "${REPORT_BUILD_FILE}"
done

# Fix markdown titles (h1 from parts must be h2 in merged file, etc.)
sed -i '' -E 's/^(#*)# /\1## /' "${REPORT_BUILD_FILE}"
sed -i '' -E 's/^## IMT /# IMT /' "${REPORT_BUILD_FILE}"

# Generate the report PDF
code "${REPORT_BUILD_FILE}"
read -p "Run 'Markdown PDF' on <${REPORT_BUILD_FILE}> in VSCode, and hit [Enter] once it's done"

# Generate the UML PDF
#mkdir -p "${UML_BUILD_DIR}"
#echo '```plantuml' > "${UML_BUILD_FILE}"
#cat "${UML_FILE}" >> "${UML_BUILD_FILE}"
#echo '```' >> "${UML_BUILD_FILE}"
#code "${UML_BUILD_FILE}"
#read -p "Run 'Markdown PDF' on <${UML_BUILD_FILE}> in VSCode, and hit [Enter] once it's done"

# Move all the files needed in the report directory
x=( '.settings' 'src' '.classpath' '.project' 'TypeCheckTests.launch' )
for f in "${x[@]}"; do
	cp -R "Explorations/4-java/${f}" "${REPORT_DIR}/${f}"
done
cp "${REPORT_PDF}" "${REPORT_DIR}/rapport_BARDON_Remi_ROURET_Lucas.pdf"
#cp "${UML_PDF}" "${REPORT_DIR}/uml.pdf"
cp "${UML_PDF}" "${REPORT_DIR}/uml.pdf"
cp "${UML_PDF%.pdf}.svg" "${REPORT_DIR}/uml.svg"
cp "${UML_PDF%.pdf}.png" "${REPORT_DIR}/uml.png"
cp 'Report/coverage.png' "${REPORT_DIR}/coverage.png"
cp 'LEXIQUE.md' "${REPORT_DIR}/LEXIQUE.md"
cp 'SEQUENTS.md' "${REPORT_DIR}/SEQUENTS.md"

# Archive the report
tar -cxvf "${REPORT_DIR}"
