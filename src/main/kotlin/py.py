#!/usr/bin/env python3
from pathlib import Path
import argparse

FILES = [
    # generator
    "com/testdata/suppliergen/generator/SupplierGenerator.kt",
    "com/testdata/suppliergen/generator/GenerationContext.kt",
    "com/testdata/suppliergen/generator/SectionBuilder.kt",
    "com/testdata/suppliergen/generator/sections/PackageSection.kt",
    "com/testdata/suppliergen/generator/sections/ImportSection.kt",
    "com/testdata/suppliergen/generator/sections/ClassHeaderSection.kt",
    "com/testdata/suppliergen/generator/sections/FieldSection.kt",
    "com/testdata/suppliergen/generator/sections/ConfiguredBuilderSection.kt",
    "com/testdata/suppliergen/generator/sections/InitializedSection.kt",
    "com/testdata/suppliergen/generator/sections/AssertEqualSection.kt",
    "com/testdata/suppliergen/generator/sections/GetMethodSection.kt",
    # model
    "com/testdata/suppliergen/model/FieldModel.kt",
    "com/testdata/suppliergen/model/SupplierClassModel.kt",
    "com/testdata/suppliergen/model/GenerationResult.kt",
    # type
    "com/testdata/suppliergen/type/KnownTypeRegistry.kt",
    "com/testdata/suppliergen/type/DefaultValueProvider.kt",
]

PSI_UTILS = "com/testdata/suppliergen/util/PsiUtils.kt"

def main():
    parser = argparse.ArgumentParser(description="Create suppliergen file structure with empty files.")
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("."),
        help="Base directory to create the structure in (default: current directory)",
    )
    parser.add_argument(
        "--with-psi-utils",
        action="store_true",
        help="Also create util/PsiUtils.kt (optional helpers)",
    )
    args = parser.parse_args()

    files = FILES.copy()
    if args.with_psi_utils:
        files.append(PSI_UTILS)

    for rel in files:
        path = args.base / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        # touch() will create the file if it doesn't exist; leave it empty
        path.touch(exist_ok=True)

    print(f"Created/verified {len(files)} files under {args.base.resolve()}")

if __name__ == "__main__":
    main()
