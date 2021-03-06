package org.codehaus.mojo.cobertura;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.cobertura.configuration.ConfigInstrumentation;
import org.codehaus.mojo.cobertura.tasks.InstrumentTask;
import org.codehaus.plexus.util.FileUtils;

/**
 * Instrument the compiled classes.
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * 
 * @goal instrument
 */
public class CoberturaInstrumentMojo
    extends AbstractCoberturaMojo
{

    /**
     * <i>Maven Internal</i>
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory factory;

    /**
     * build up a command line from the parameters and run
     * Cobertura to instrument the code.
     */
    public void execute()
        throws MojoExecutionException
    {
        File instrumentedDirectory = new File(project.getBuild().getDirectory(), "generated-classes/cobertura");
        
        if ( !instrumentedDirectory.exists() )
        {
            instrumentedDirectory.mkdirs();
        }

        /* ensure that instrumentation config is set here, not via maven
         * plugin api @required attribute, as this is not a required 
         * object from the pom configuration's point of view. */
        if ( instrumentation == null )
        {
            instrumentation = new ConfigInstrumentation();
        }

        /* ensure that the default includes is set */
        if ( instrumentation.getIncludes().isEmpty() )
        {
            instrumentation.addInclude( "**/*.class" );
        }

        
        // Copy all of the classes into the instrumentation basedir.
        try
        {
            FileUtils.copyDirectoryStructure( new File( project.getBuild().getOutputDirectory() ), instrumentedDirectory );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to prepare instrumentation directory.", e );
        }
        
        instrumentation.setBasedir( instrumentedDirectory );
        
        // Execute the instrumentation task.
        InstrumentTask task = new InstrumentTask();
        setTaskDefaults( task );
        task.setConfig( instrumentation );
        task.setDestinationDir( instrumentedDirectory );
        task.setDataFile( dataFile );

        task.execute();

        addCoberturaDependenciesToTestClasspath();

        System.setProperty( "net.sourceforge.cobertura.datafile", dataFile.getPath() );
        project.getBuild().setOutputDirectory( instrumentedDirectory.getPath() );
        System.setProperty( "project.build.outputDirectory", instrumentedDirectory.getPath() );
    }
    
    private void addCoberturaDependenciesToTestClasspath()
        throws MojoExecutionException
    {
        Artifact coberturaArtifact = null;
        Artifact log4jArtifact = null;

        Map pluginArtifactMap = ArtifactUtils.artifactMapByVersionlessId( pluginClasspathList );

        coberturaArtifact = (Artifact) pluginArtifactMap.get( "cobertura:cobertura" );
        log4jArtifact = (Artifact) pluginArtifactMap.get( "log4j:log4j" );

        if ( coberturaArtifact == null )
        {
            throw new MojoExecutionException( "Couldn't find 'cobertura' artifact in plugin dependencies" );
        }

        if ( log4jArtifact == null )
        {
            throw new MojoExecutionException( "Couldn't find 'log4j' artifact in plugin dependencies" );
        }

        Map projectArtifactMap = ArtifactUtils.artifactMapByArtifactId( project.getDependencyArtifacts() );

        // This step is done in order to set scope to test.
        coberturaArtifact = artifactScopeToTest( coberturaArtifact );
        log4jArtifact = artifactScopeToTest( log4jArtifact );

        Set set = new HashSet( this.project.getDependencyArtifacts() );
        set.add( coberturaArtifact );
        if ( !projectArtifactMap.containsKey( "log4j" ) )
        {
            // Add only if it's not there.
            set.add( log4jArtifact );
        }
        this.project.setDependencyArtifacts( set );
    }

    private Artifact artifactScopeToTest( Artifact artifact )
    {
        return factory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                                       Artifact.SCOPE_TEST, artifact.getType() );
    }

}
