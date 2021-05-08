package org.codehaus.mojo.cobertura.tasks;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * The Report Task. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public class ReportTask
    extends AbstractTask
{
    private File dataFile = null;

    private File outputDirectory = null;

    private String outputFormat;

    private File sourceDirectory = null;

    /**
     * Create ReportTask.
     */
    public ReportTask()
    {
        super( "net.sourceforge.cobertura.reporting.Main" );
    }

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            outputDirectory.mkdirs();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error invoking Cobertura, unable to create output directory.", e );
        }

        if ( sourceDirectory != null )
        {
            cmdLineArgs.addArg( "--source", sourceDirectory.getAbsolutePath() );
        }

        if ( outputDirectory != null )
        {
            cmdLineArgs.addArg( "--destination", outputDirectory.getAbsolutePath() );
        }

        if ( dataFile != null )
        {
            cmdLineArgs.addArg( "--datafile", dataFile.getAbsolutePath() );
        }

        if ( StringUtils.isNotEmpty( outputFormat ) )
        {
            cmdLineArgs.addArg( "--format", outputFormat );
        }

        int returnCode = executeJava();

        // Check the return code and print a message
        if ( returnCode == 0 )
        {
            getLog().info( "Cobertura Report generation was successful." );
        }
        else
        {
            throw new MojoExecutionException( "Unable to generate Cobertura Report for project." );
        }
    }

    /**
     * @return Returns the dataFile.
     */
    public File getDataFile()
    {
        return dataFile;
    }

    /**
     * @return Returns the outputDirectory.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @return Returns the outputFormat.
     */
    public String getOutputFormat()
    {
        return outputFormat;
    }

    /**
     * @return Returns the sourceDirectory.
     */
    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    /**
     * @param dataFile The dataFile to set.
     */
    public void setDataFile( File dataFile )
    {
        this.dataFile = dataFile;
    }

    /**
     * @param outputDirectory The outputDirectory to set.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @param outputFormat The outputFormat to set.
     */
    public void setOutputFormat( String outputFormat )
    {
        this.outputFormat = outputFormat;
    }

    /**
     * @param sourceDirectory The sourceDirectory to set.
     */
    public void setSourceDirectory( File sourceDirectory )
    {
        this.sourceDirectory = sourceDirectory;
    }
}
