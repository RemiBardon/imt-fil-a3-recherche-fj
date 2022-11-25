#!/bin/bash

REPORT_DIR='Report/out/2022-2023-FILA3-Capitrain-BARDON-Remi-ROURET-Lucas'
REPORT_BUILD_DIR='Report/out/REPORT'
REPORT_BUILD_FILE="${REPORT_BUILD_DIR}/README.md"
REPORT_PARTS=( 'Report/LOG.md' )

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
mv "${REPORT_BUILD_DIR}/README.pdf" "${REPORT_DIR}/rapport_BARDON_Remi_ROURET_Lucas.pdf"
