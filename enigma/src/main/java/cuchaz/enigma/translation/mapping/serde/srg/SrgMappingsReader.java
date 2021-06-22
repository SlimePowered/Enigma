package cuchaz.enigma.translation.mapping.serde.srg;

import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;
import cuchaz.enigma.translation.mapping.serde.MappingSaveParameters;
import cuchaz.enigma.translation.mapping.serde.MappingsReader;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.translation.mapping.tree.HashEntryTree;
import cuchaz.enigma.translation.representation.MethodDescriptor;
import cuchaz.enigma.translation.representation.TypeDescriptor;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.FieldEntry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;
import org.bookmc.srg.SrgProcessor;
import org.bookmc.srg.output.MappedClass;
import org.bookmc.srg.output.MappedField;
import org.bookmc.srg.output.MappedMethod;
import org.bookmc.srg.output.SrgOutput;

import java.io.IOException;
import java.nio.file.Path;

public enum SrgMappingsReader implements MappingsReader {
    INSTANCE;

    @Override
    public EntryTree<EntryMapping> read(Path path, ProgressListener progress, MappingSaveParameters saveParameters) throws MappingParseException, IOException {
        SrgProcessor processor = new SrgProcessor(path.toFile());
        SrgOutput output = processor.process();


        EntryTree<EntryMapping> tree = new HashEntryTree<>();

        for (MappedMethod method : output.getMethods()) {
            tree.insert(new MethodEntry(new ClassEntry(method.getUnmappedOwner()), method.getUnmappedName(), new MethodDescriptor(method.getUnmappedDesc())), new EntryMapping(method.getMappedName()));
        }

        for (MappedField field : output.getFields()) {
            tree.insert(new FieldEntry(new ClassEntry(field.getMappedOwner()), field.getMappedName(), TypeDescriptor.of(field.getMappedOwner())), new EntryMapping(field.getUnmappedName()));
        }

        for (MappedClass clazz : output.getClasses()) {
            tree.insert(new ClassEntry(clazz.getUnmappedName()), new EntryMapping(clazz.getMappedName()));
        }

        return tree;
    }
}
